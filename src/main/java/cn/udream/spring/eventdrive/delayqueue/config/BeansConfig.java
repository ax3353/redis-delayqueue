package cn.udream.spring.eventdrive.delayqueue.config;

import cn.udream.spring.eventdrive.delayqueue.core.DelayBucket;
import cn.udream.spring.eventdrive.delayqueue.core.JobPostProcessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: Bean配置
 *
 * @author kun.zhu
 * @date 2020/5/28 18:03
 */
@Configuration
public class BeansConfig {

    private final int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

    private final AtomicInteger SchedThreadCounter = new AtomicInteger(1);

    private final AtomicInteger executeThreadCounter = new AtomicInteger(1);

    /**
     * redisTemplate 默认序列化使用的jdkSerializeable, 存储为二进制字节码, 所以自定义序列化类
     */
    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"})
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer<?> serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        serializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(corePoolSize, r -> {
            if (SchedThreadCounter.get() == corePoolSize) {
                SchedThreadCounter.set(1);
            }
            Thread thread = new Thread(r, "delay-thread-" + SchedThreadCounter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean
    public ExecutorService executeThreadPool() {
        return Executors.newFixedThreadPool(corePoolSize, r -> {
            if (executeThreadCounter.get() == corePoolSize) {
                executeThreadCounter.set(1);
            }
            Thread thread = new Thread(r, "exec-thread-" + executeThreadCounter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayBucket delayBucket(RedisTemplate<String, String> redisTemplate) {
        return new DelayBucket(redisTemplate);
    }

    @Bean
    public JobPostProcessor jobPostProcessor(GlobalConfig globalConfig) {
        return new JobPostProcessor(globalConfig);
    }
}
