package cn.udream.spring.eventdrive.delayqueue.core;

import lombok.Data;

import java.io.Serializable;


/**
 * @author kun.zhu
 * @date 2020/5/28 18:14
 */
@Data
public class Job<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -7291310805339987123L;

    /**
     * Job的唯一标识
     */
    private String id;

    /**
     * Job类型
     */
    private String topic;

    /**
     * Job需要延迟的时间。单位：秒
     */
    private transient int delay;

    /**
     * Job执行超时时间。单位：秒
     */
    private transient int ttr;

    /**
     * Job的内容
     */
    private T body;

    /**
     * 间隔频率
     */
    private int[] intervals;

}
