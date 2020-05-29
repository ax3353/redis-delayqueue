package cn.udream.spring.eventdrive.delayqueue.example;

import cn.udream.spring.eventdrive.delayqueue.consts.ExecuteState;
import cn.udream.spring.eventdrive.delayqueue.core.JobCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 描述: 测试topic
 * @author kun.zhu
 * @date 2020/5/28 18:10
 */
@Component
@Slf4j
public class TestWaitingNotifyListener implements JobCallback<PayFlow> {

    @Override
    public String topic() {
        return "pay-notify";
    }

    @Override
    public ExecuteState execute(PayFlow payFlow) {
        boolean b = new Random().nextBoolean();
        log.info("execute: {}, result: {}!!!", payFlow, b);
        return b ? ExecuteState.SUCCESS : ExecuteState.FAILURE;
    }

}
