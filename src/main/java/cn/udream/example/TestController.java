package cn.udream.example;

import cn.udream.delayqueue.core.DelayBucket;
import cn.udream.delayqueue.core.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description: 测试
 * @author: kun.zhu
 * @create: 2019-04-24 16:59
 **/
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DelayBucket delayBucket;

    @GetMapping("pushObject")
    public String pushObject() {
        int delay = new Random().nextInt(10);
        Job<PayFlow> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-pay-notify");
        job.setDelay(delay);

        PayFlow payFlow = new PayFlow(1234560L, 1234567L, new BigDecimal("123.98"), 1, LocalDateTime.now());
        PayFlow.Abcd abcd = new PayFlow.Abcd("s", 100000L, 124);
        payFlow.setAbcd(abcd);
        List<PayFlow.Abcd> abcds = new ArrayList<>();
        abcds.add(abcd);
        abcds.add(abcd);
        payFlow.setAbcds(abcds);

        job.setBody(payFlow);
        job.setIntervals(new int[]{0, 0, 12, 30});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushString")
    public String pushString() {
        int delay = new Random().nextInt(10);
        Job<String> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-string");
        job.setDelay(delay);
        job.setBody("0123456789");
        job.setIntervals(new int[]{0, 0, 12, 30});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushLong")
    public String pushLong() {
        int delay = new Random().nextInt(10);
        Job<Long> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-long");
        job.setDelay(delay);
        job.setBody(123456789L);
        job.setIntervals(new int[]{0, 0, 3, 6});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushInteger")
    public String pushInteger() {
        int delay = new Random().nextInt(3);
        Job<Integer> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-int");
        job.setDelay(delay);
        job.setBody(1234567);
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushDouble")
    public String pushDouble() {
        int delay = new Random().nextInt(3);
        Job<Double> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-double");
        job.setDelay(delay);
        job.setBody(1234567.98D);
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushBoolean")
    public String pushBoolean() {
        int delay = new Random().nextInt(3);
        Job<Boolean> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-boolean");
        job.setDelay(delay);
        job.setBody(Boolean.FALSE);
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushCharacter")
    public String pushCharacter() {
        int delay = new Random().nextInt(3);
        Job<Character> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-char");
        job.setDelay(delay);
        job.setBody(Character.valueOf('c'));
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushByte")
    public String pushByte() {
        int delay = new Random().nextInt(3);
        Job<Byte> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-byte");
        job.setDelay(delay);
        job.setBody(new Byte("1"));
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("pushShort")
    public String pushShort() {
        int delay = new Random().nextInt(3);
        Job<Short> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("test-short");
        job.setDelay(delay);
        job.setBody(Short.valueOf("123"));
        job.setIntervals(new int[]{0, 2});
        boolean success = delayBucket.push(job);
        return "push result: " + success;
    }

    @GetMapping("keys")
    public Set<String> keys() {
        return redisTemplate.keys("*");
    }

    @GetMapping("flushall")
    public String flushall() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
        return "flush all";
    }
}
