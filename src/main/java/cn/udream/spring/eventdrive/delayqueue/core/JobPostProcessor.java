package cn.udream.spring.eventdrive.delayqueue.core;

import cn.udream.spring.eventdrive.delayqueue.config.GlobalConfig;
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
        if (bean instanceof JobCallback) {
            JobCallback jobCallback = (JobCallback) bean;
            callbackMap.put(jobCallback.topic(), jobCallback);
        }
        return bean;
    }
}
