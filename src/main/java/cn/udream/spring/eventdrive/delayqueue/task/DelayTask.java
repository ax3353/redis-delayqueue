package cn.udream.spring.eventdrive.delayqueue.task;

import cn.udream.spring.eventdrive.delayqueue.config.GlobalConfig;
import cn.udream.spring.eventdrive.delayqueue.consts.ExecuteState;
import cn.udream.spring.eventdrive.delayqueue.consts.KEYS;
import cn.udream.spring.eventdrive.delayqueue.core.Callback;
import cn.udream.spring.eventdrive.delayqueue.core.MetaJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:11
 */
@Slf4j
public class DelayTask implements Runnable {

    private final RedisTemplate<String, String> redisTemplate;

    private final ExecutorService executeThreadPool;

    private final GlobalConfig globalConfig;

    public DelayTask(RedisTemplate<String, String> redisTemplate, ExecutorService executeThreadPool, GlobalConfig globalConfig) {
        this.redisTemplate = redisTemplate;
        this.executeThreadPool = executeThreadPool;
        this.globalConfig = globalConfig;
    }

    @Override
    public void run() {
        long now = Instant.now().getEpochSecond();
        long begin = now - 24 * 3600;
        Set<String> objects = redisTemplate.boundZSetOps(KEYS.DELAY_KEY).rangeByScore(begin, now);
        if (CollectionUtils.isEmpty(objects)) {
            return;
        }

        log.debug("waiting notify queue has objects: {}", objects);

        objects.stream()
                .filter(globalConfig::isWaitProcessing)
                .map(k -> (Runnable) () -> executeCallback(k))
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

        T body = null;
        if (ClassUtils.isPrimitiveOrWrapper(clz)) {
            body = (T) this.convert(clz, metaJob.getBody());
        } else {
            try {
                body = new ObjectMapper().readValue(metaJob.getBody(), clz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ExecuteState executeState = callback.execute(body);
        if (ExecuteState.SUCCESS.equals(executeState)) {
            this.deleteJob(key);
        } else {
            this.retry(key, metaJob);
        }

        globalConfig.processed(key);
    }

    private MetaJob toMetaJob(String key) {
        Object o = redisTemplate.opsForHash().get(KEYS.HASH_KEY, key);
        if (o == null) {
            return null;
        }

        try {
            return new ObjectMapper().readValue(o.toString(), MetaJob.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 回调执行失败触发重试，如果超过重试次数则删除Job，否则重新计算执行时间再放入等待队列
     */
    private void retry(String key, MetaJob metaJob) {
        metaJob.addRetry();

        if (metaJob.getHasRetry() > metaJob.getIntervals().length) {
            this.deleteJob(key);
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

        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();

        redisTemplate.opsForZSet().add(KEYS.DELAY_KEY, jobId, executeTime);
        try {
            String body = new ObjectMapper().writeValueAsString(metaJob);
            redisTemplate.opsForHash().put(KEYS.HASH_KEY, jobId, body);
        } catch (Exception e) {
            log.error("Json Processing Exception", e);
            redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, jobId);
        }

        redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
        log.warn("回调执行失败, 重新放入队列 jobId: {}, executeTime: {}", jobId, executeTime);
    }

    private void deleteJob(String key) {
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();

        redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, key);
        redisTemplate.opsForHash().delete(KEYS.HASH_KEY, key);

        redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
    }

    private Class getActualType(Callback callback) {
        try {
            Type superClass = callback.getClass().getGenericSuperclass();
            if (superClass instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) superClass;
                Type rawType = p.getRawType(); // 父类实际类型
                Type[] actualTypes = p.getActualTypeArguments(); //父类泛型实际类型
                System.out.println("父类类型： " + rawType.toString() + " 父类泛型实际类型：" + Arrays.toString(actualTypes));
                return (Class) actualTypes[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Object.class;
    }

    private Object convert(Class clazz, String value) {
        if (String.class == clazz) return value;
        if (Boolean.class == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz) return Byte.parseByte(value);
        if (Short.class == clazz) return Short.parseShort(value);
        if (Integer.class == clazz) return Integer.parseInt(value);
        if (Long.class == clazz) return Long.parseLong(value);
        if (Float.class == clazz) return Float.parseFloat(value);
        if (Double.class == clazz) return Double.parseDouble(value);
        return value;
    }
}
