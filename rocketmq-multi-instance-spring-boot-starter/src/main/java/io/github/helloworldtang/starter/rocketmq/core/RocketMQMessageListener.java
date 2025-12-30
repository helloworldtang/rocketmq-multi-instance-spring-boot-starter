package io.github.helloworldtang.starter.rocketmq.core;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * Interface to be implemented by message listeners.
 */
public interface RocketMQMessageListener {
    void onMessage(MessageExt message);
}
