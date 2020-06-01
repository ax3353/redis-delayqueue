package cn.udream.spring.eventdrive.delayqueue.core;

import cn.udream.spring.eventdrive.delayqueue.consts.KEYS;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 描述: bucket
 *
 * @author kun.zhu
 * @date 2020/5/28 18:13
 */
@Slf4j
public class DelayBucket {

    private static final SerializeConfig sc = SerializeConfig.globalInstance;

    static {
        sc.put(Boolean.class, BooleanCodec.instance);
        sc.put(Boolean.TYPE, BooleanCodec.instance);

        sc.put(Byte.class, IntegerCodec.instance);
        sc.put(Byte.TYPE, IntegerCodec.instance);

        sc.put(Short.class, IntegerCodec.instance);
        sc.put(Short.TYPE, IntegerCodec.instance);

        sc.put(Integer.class, IntegerCodec.instance);
        sc.put(Integer.TYPE, IntegerCodec.instance);

        sc.put(Long.class, LongCodec.instance);
        sc.put(Long.TYPE, LongCodec.instance);

        sc.put(Float.class, FloatCodec.instance);
        sc.put(Float.TYPE, FloatCodec.instance);

        sc.put(Double.class, DoubleSerializer.instance);
        sc.put(Double.TYPE, DoubleSerializer.instance);

        sc.put(Character.class, CharacterCodec.instance);
        sc.put(Character.TYPE, CharacterCodec.instance);
    }

    private final RedisTemplate<String, Object> redisTemplate;

    public DelayBucket(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Object> push(Job<?> job) {
        String jobId = job.getId();
        String topic = job.getTopic();
        Serializable body = job.getBody();

        if (!StringUtils.hasText(jobId) || !StringUtils.hasText(topic) || Objects.isNull(body)) {
            throw new IllegalArgumentException("jobId, topic, body不能为空");
        }

        long now = Instant.now().getEpochSecond();
        long executeTime = now + TimeUnit.SECONDS.toSeconds(job.getDelay());

        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForZSet().add(KEYS.DELAY_KEY, jobId, executeTime);

        MetaJob metaJob = new MetaJob();
        metaJob.setId(jobId);
        metaJob.setTopic(topic);
        metaJob.setHasRetry(0);
        metaJob.setIntervals(job.getIntervals());
        try {
            metaJob.setBody(JSON.toJSONString(body, sc));
            redisTemplate.opsForHash().put(KEYS.HASH_KEY, jobId, metaJob);
        } catch (Exception e) {
            log.error("Convert MetaJob To Json Exception", e);
            redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, jobId);
        }

        List<Object> objects = redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
        return objects;
    }
}
