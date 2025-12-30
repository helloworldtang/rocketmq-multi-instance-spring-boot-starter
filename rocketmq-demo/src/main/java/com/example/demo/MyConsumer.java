package com.example.demo;

import io.github.helloworldtang.starter.rocketmq.annotation.RocketMQListener;
import io.github.helloworldtang.starter.rocketmq.core.RocketMQMessageListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

@Component
@RocketMQListener(
        instanceId = "instance1",
        topic = "TopicTest",
        consumerGroupId = "my-consumer-group2",
        tags = "*"
)
public class MyConsumer implements RocketMQMessageListener {

    @Override
    public void onMessage(MessageExt message) {
        System.out.println("Received message: " + new String(message.getBody()));
    }

}
