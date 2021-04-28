package cn.udream.delayqueue.core;

import cn.udream.delayqueue.config.GlobalConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

/**
 * 描述: Job后置处理器，用于注册Job的回调方法
 * @author kun.zhu
 * @date 2020/5/28 11:24
 */
public class JobPostProcessor implements BeanPostProcessor {

    private final Map<String, Callback> callbackMap;

    public JobPostProcessor(GlobalConfig globalConfig) {
        this.callbackMap = globalConfig.getCallbackMap();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Callback) {
            Callback callback = (Callback) bean;
            callbackMap.put(callback.topic(), callback);
        }
        return bean;
    }
}
