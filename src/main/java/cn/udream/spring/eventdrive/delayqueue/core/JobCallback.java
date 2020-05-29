package cn.udream.spring.eventdrive.delayqueue.core;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:14
 */
public interface JobCallback<T> extends Callback<T> {

    String topic();
}
