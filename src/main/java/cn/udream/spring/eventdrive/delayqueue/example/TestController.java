package cn.udream.spring.eventdrive.delayqueue.example;

import cn.udream.spring.eventdrive.delayqueue.core.DelayBucket;
import cn.udream.spring.eventdrive.delayqueue.core.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * @description: 测试
 * @author: kun.zhu
 * @create: 2019-04-24 16:59
 **/
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DelayBucket delayBucket;

    @GetMapping("push")
    public String push() {
        int delay = new Random().nextInt(10);
        Job<PayFlow> job = new Job<>();
        job.setId(Instant.now().getEpochSecond() + "");
        job.setTopic("pay-notify");
        job.setDelay(delay);
        job.setTtr(60);
        job.setBody(new PayFlow(1234560L, 1234567L, new BigDecimal("123.98"), 1, LocalDateTime.now()));
        job.setIntervals(new int[]{0, 0, 12, 30});
        List<Object> objects = delayBucket.push(job);
        log.info("push success, objects: {}", objects);
        return "push success";
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
