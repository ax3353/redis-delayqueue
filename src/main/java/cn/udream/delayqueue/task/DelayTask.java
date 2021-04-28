package cn.udream.delayqueue.task;

import cn.udream.delayqueue.config.GlobalConfig;
import cn.udream.delayqueue.consts.ExecuteState;
import cn.udream.delayqueue.consts.KEYS;
import cn.udream.delayqueue.core.Callback;
import cn.udream.delayqueue.core.MetaJob;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:11
 */
@Slf4j
public class DelayTask implements Runnable {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ExecutorService executeThreadPool;

    private final GlobalConfig globalConfig;

    public DelayTask(RedisTemplate<String, Object> redisTemplate, ExecutorService executeThreadPool, GlobalConfig globalConfig) {
        this.redisTemplate = redisTemplate;
        this.executeThreadPool = executeThreadPool;
        this.globalConfig = globalConfig;
    }

    @Override
    public void run() {
        long now = Instant.now().getEpochSecond();
        long begin = now - 24 * 3600;
        Set<Object> objects = redisTemplate.boundZSetOps(KEYS.DELAY_KEY).rangeByScore(begin, now);
        if (CollectionUtils.isEmpty(objects)) {
            return;
        }

        log.debug("delay queue has objects: {}", objects);

        objects.stream()
                .filter(k -> globalConfig.isWaitProcessing(k.toString()))
                .map(k -> (Runnable) () -> executeCallback(k.toString()))
                .forEach(executeThreadPool::submit);
    }

    private <T extends Serializable> void executeCallback(String key) {
        log.debug("executing callback: {}", key);
        globalConfig.processing(key);

        MetaJob metaJob = this.toMetaJob(key);
        if (metaJob == null) {
            return;
        }

        String topic = metaJob.getTopic();
        Map<String, Callback> callbackMap = globalConfig.getCallbackMap();
        if (!callbackMap.containsKey(topic)) {
            return;
        }

        Callback<T> callback = (Callback<T>) callbackMap.get(topic);
        Class<T> clz = this.getActualType(callback);

        T body;
        if (ClassUtils.isPrimitiveOrWrapper(clz)) {
            body = (T) this.convert(clz, metaJob.getBody());
        } else {
            body = JSON.parseObject(metaJob.getBody(), clz);
        }

        ExecuteState executeState = callback.execute(body);
        if (ExecuteState.SUCCESS.equals(executeState)) {
            this.deleteJob(key);
            log.info("callback execute success, clear key: {}", key);
        } else {
            this.retry(key, metaJob);
        }

        globalConfig.processed(key);
    }

    private MetaJob toMetaJob(String key) {
        Object o = redisTemplate.opsForHash().get(KEYS.HASH_KEY, key);
        return o instanceof MetaJob ? (MetaJob) o : null;
    }

    /**
     * 回调执行失败触发重试，如果超过重试次数则删除Job，否则重新计算执行时间再放入等待队列
     */
    private void retry(String key, MetaJob metaJob) {
        metaJob.addRetry();

        if (metaJob.getHasRetry() > metaJob.getIntervals().length) {
            this.deleteJob(key);
            log.warn("Job: {} 重试次数已用完", metaJob.getId());
            return;
        }
        this.rePush(metaJob);
    }

    private void rePush(MetaJob metaJob) {
        int length = metaJob.getIntervals().length;
        int hasRetry = metaJob.getHasRetry();

        int delay = metaJob.getIntervals()[(hasRetry >= length) ? length - 1 : hasRetry];
        long executeTime = Instant.now().getEpochSecond() + delay;
        String jobId = metaJob.getId();

        try {
            redisTemplate.opsForHash().put(KEYS.HASH_KEY, jobId, metaJob);
        } catch (Exception e) {
            log.error("Job RePush Exception -> {}", metaJob, e);
            redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, jobId);
            redisTemplate.opsForHash().delete(KEYS.HASH_KEY, jobId);
        }
        log.warn("回调第{}次执行失败, 重新放入队列 jobId: {}, executeTime: {}", hasRetry, jobId, executeTime);
    }

    private void deleteJob(String key) {
        redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, key);
        redisTemplate.opsForHash().delete(KEYS.HASH_KEY, key);
    }

    private Class getActualType(Callback callback) {
        Type[] superClass = callback.getClass().getGenericInterfaces();
        for (Type type : superClass) {
            if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
                ParameterizedType p = (ParameterizedType) type;
                Type[] actualTypes = p.getActualTypeArguments();
                return (Class) actualTypes[0];
            }
        }
        return Object.class;
    }

    private Object convert(Class clazz, String value) {
        try {
            if (Boolean.class == clazz) return Boolean.parseBoolean(value);
            if (Byte.class == clazz) return Byte.parseByte(value);
            if (Short.class == clazz) return Short.parseShort(value);
            if (Integer.class == clazz) return Integer.parseInt(value);
            if (Long.class == clazz) return Long.parseLong(value);
            if (Float.class == clazz) return Float.parseFloat(value);
            if (Double.class == clazz) return Double.parseDouble(value);
            if (Character.class == clazz) return value.charAt(0);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }
}
