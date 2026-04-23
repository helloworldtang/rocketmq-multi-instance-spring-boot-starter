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
            DefaultMQProducer producer = RocketMQProducerContainer.get("instance1");
            Message message = new Message("TopicTest", "TagA", msg.getBytes());
            producer.send(message);
            return "Sent: " + msg;
        } catch (IllegalArgumentException e) {
            return "Failed: " + e.getMessage();
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }
}
