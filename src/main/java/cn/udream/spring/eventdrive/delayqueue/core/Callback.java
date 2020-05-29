package cn.udream.spring.eventdrive.delayqueue.core;

import cn.udream.spring.eventdrive.delayqueue.consts.ExecuteState;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:10
 */
@FunctionalInterface
public interface Callback<T> {

    ExecuteState execute(T t);
}
