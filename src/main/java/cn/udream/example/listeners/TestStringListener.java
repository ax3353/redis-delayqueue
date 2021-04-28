package cn.udream.example.listeners;

import cn.udream.delayqueue.consts.ExecuteState;
import cn.udream.delayqueue.core.Callback;
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
public class TestStringListener implements Callback<String> {

    @Override
    public String topic() {
        return "test-string";
    }

    @Override
    public ExecuteState execute(String aLong) {
        boolean b = new Random().nextBoolean();
        log.info("TestStringListener Execute: {}, Result: {}!!!", aLong, b);
        return b ? ExecuteState.SUCCESS : ExecuteState.FAILURE;
    }
}
