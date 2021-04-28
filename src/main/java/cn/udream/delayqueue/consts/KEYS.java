package cn.udream.delayqueue.consts;

/**
 * 描述: key定义
 * @author kun.zhu
 * @date 2020/5/28 18:12
 */
public interface KEYS {

    String KEY_PREFIX = "DELAY_QUEUE_";

    String DELAY_KEY = KEY_PREFIX + "DELAY";

    String HASH_KEY = KEY_PREFIX + "HASH";
}
