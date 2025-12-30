package com.example.demo;

import io.github.helloworldtang.starter.rocketmq.core.RocketMQProducerContainer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    // @Autowired
    // private RocketMQProducerContainer producerContainer;

    @GetMapping("/send")
    public String send(@RequestParam("msg") String msg) {
        try {
            // Use static method to get producer
            DefaultMQProducer producer = RocketMQProducerContainer.get("instance1");
            if (producer == null) {
                return "Failed: instance1 producer not found";
            }
            Message message = new Message("TopicTest", "TagA", msg.getBytes());
            producer.send(message);
            return "Sent: " + msg;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed: " + e.getMessage();
        }
    }
}
