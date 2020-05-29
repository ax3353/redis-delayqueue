package cn.udream.spring.eventdrive.delayqueue.config;

import cn.udream.spring.eventdrive.delayqueue.core.Callback;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 描述: 全局配置, 主要用于共享全局变量
 * @author kun.zhu
 * @date 2020/5/28 18:09
 */
@Component
@Data
public class GlobalConfig {

    private Set<String> processedKeys = new CopyOnWriteArraySet<>();

    private Map<String, Callback> callbackMap = new ConcurrentHashMap<>();

    /**
     * 描述: 是否正在处理中
     * @author kun.zhu
     * @date 2020/5/28 10:00
     */
    public boolean isProcessing(String key) {
        return processedKeys.contains(key);
    }

    /**
     * 描述: 是否等待处理
     * @author kun.zhu
     * @date 2020/5/28 10:01
     */
    public boolean isWaitProcessing(String key) {
        return !isProcessing(key);
    }

    /**
     * 描述: 正在处理中
     * @author kun.zhu
     * @date 2020/5/28 10:02
     */
    public void processing(String key) {
        processedKeys.add(key);
    }

    /**
     * 描述: 已处理
     * @author kun.zhu
     * @date 2020/5/28 10:03
     */
    public void processed(String key) {
        processedKeys.remove(key);
    }
}
