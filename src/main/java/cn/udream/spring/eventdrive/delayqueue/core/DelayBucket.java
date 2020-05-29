package cn.udream.spring.eventdrive.delayqueue.core;

import cn.udream.spring.eventdrive.delayqueue.consts.KEYS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 描述: bucket
 * @author kun.zhu
 * @date 2020/5/28 18:13
 */
@Slf4j
public class DelayBucket {

    private final RedisTemplate<String, String> redisTemplate;

    public DelayBucket(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Object> push(Job<?> job) {
        long now = Instant.now().getEpochSecond();
        long executeTime = now + TimeUnit.SECONDS.toSeconds(job.getDelay());
        String jobId = job.getId();

        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForZSet().add(KEYS.DELAY_KEY, jobId, executeTime);

        ObjectMapper mapper = new ObjectMapper();
        MetaJob metaJob = new MetaJob();
        metaJob.setId(jobId);
        metaJob.setTopic(job.getTopic());
        metaJob.setTtr(job.getTtr());
        metaJob.setHasRetry(0);
        metaJob.setIntervals(job.getIntervals());
        try {
            metaJob.setBody(mapper.writeValueAsString(job.getBody()));
            String metaJobHashValue = mapper.writeValueAsString(metaJob);
            redisTemplate.opsForHash().put(KEYS.HASH_KEY, jobId, metaJobHashValue);
        } catch (Exception e) {
            log.error("Convert MetaJob To Json Exception", e);
            redisTemplate.opsForZSet().remove(KEYS.DELAY_KEY, jobId);
        }

        List<Object> objects = redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
        return objects;
    }

}
