package cn.udream.delayqueue.core;

import cn.udream.delayqueue.consts.ExecuteState;

/**
 * @author kun.zhu
 * @date 2020/5/28 18:10
 */
public interface Callback<T> {

    String topic();

    ExecuteState execute(T t);
}
