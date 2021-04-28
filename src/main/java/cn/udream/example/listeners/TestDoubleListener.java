package cn.udream.example.listeners;

import cn.udream.delayqueue.consts.ExecuteState;
import cn.udream.delayqueue.core.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 描述: <>
 * @author kun.zhu
 * @date 2021/4/28 17:09
 */
@Component
@Slf4j
public class TestDoubleListener implements Callback<Double> {

    @Override
    public String topic() {
        return "test-double";
    }

    @Override
    public ExecuteState execute(Double aDouble) {
        boolean b = new Random().nextBoolean();
        log.info("TestLongListener Execute: {}, Result: {}!!!", aDouble, b);
        return b ? ExecuteState.SUCCESS : ExecuteState.FAILURE;
    }
}
