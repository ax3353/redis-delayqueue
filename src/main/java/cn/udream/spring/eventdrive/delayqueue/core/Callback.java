package cn.udream.spring.eventdrive.delayqueue.core;

import cn.udream.spring.eventdrive.delayqueue.consts.ExecuteState;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:10
 */
public interface Callback<T> {

    String topic();

    ExecuteState execute(T t);
}
