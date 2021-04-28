package cn.udream.example.listeners;

import cn.udream.delayqueue.consts.ExecuteState;
import cn.udream.delayqueue.core.Callback;
import cn.udream.example.PayFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 描述: 测试topic
 *
 * @author kun.zhu
 * @date 2020/5/28 18:10
 */
@Component
@Slf4j
public class TestPayNotifyListener implements Callback<PayFlow> {

    @Override
    public String topic() {
        return "test-pay-notify";
    }

    @Override
    public ExecuteState execute(PayFlow payFlow) {
        boolean b = new Random().nextBoolean();
        log.info("TestPayNotifyListener Execute: {}, Result: {}!!!", payFlow, b);
        return b ? ExecuteState.SUCCESS : ExecuteState.FAILURE;
    }

}
