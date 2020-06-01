package cn.udream.spring.eventdrive.delayqueue.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述: job元数据
 * @author kun.zhu
 * @date 2020/5/28 18:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaJob implements Serializable {
    private static final long serialVersionUID = -1291310805339987123L;

    /**
     * Job的唯一标识
     */
    private String id;

    /**
     * Job类型
     */
    private String topic;

    /**
     * Job的内容, json格式存储
     */
    private String body;

    /**
     * 已经重试到第几次
     */
    private int hasRetry;

    /**
     * 间隔频率
     */
    private int[] intervals;

    public void addRetry() {
        this.hasRetry++;
    }
}
