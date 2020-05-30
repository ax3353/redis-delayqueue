package cn.udream.spring.eventdrive.delayqueue.config;

import cn.udream.spring.eventdrive.delayqueue.core.DelayBucket;
import cn.udream.spring.eventdrive.delayqueue.core.JobPostProcessor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        //key采用String序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //value采用fast-json序列化方式。
        FastJson2JsonRedisSerializer<Object> serializer = new FastJson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
        public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
        private Class<T> clazz;

        public FastJson2JsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
        }

        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, DEFAULT_CHARSET);
            return JSON.parseObject(str, clazz, Feature.SupportAutoType);
        }
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
    public DelayBucket delayBucket(RedisTemplate<String, Object> redisTemplate) {
        return new DelayBucket(redisTemplate);
    }

    @Bean
    public JobPostProcessor jobPostProcessor(GlobalConfig globalConfig) {
        return new JobPostProcessor(globalConfig);
    }
}
