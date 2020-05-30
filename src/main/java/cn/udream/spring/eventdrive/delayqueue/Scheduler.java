package cn.udream.spring.eventdrive.delayqueue;

import cn.udream.spring.eventdrive.delayqueue.config.GlobalConfig;
import cn.udream.spring.eventdrive.delayqueue.task.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 调度器, 启动入口类
 * @author kun.zhu
 * @date 2020/5/28 18:11
 */
@Component
@Slf4j
public class Scheduler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ScheduledExecutorService scheduledThreadPool;
    @Autowired
    private ExecutorService executeThreadPool;
    @Autowired
    private GlobalConfig globalConfig;

    @PostConstruct
    public void postConstruct() {
        log.info("Bucket Scanning...");
        scheduledThreadPool.scheduleAtFixedRate(new DelayTask(redisTemplate, executeThreadPool, globalConfig), 0, 1000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduledThreadPool.shutdown();
        executeThreadPool.shutdown();
        RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        log.info("release resource");
    }
}
