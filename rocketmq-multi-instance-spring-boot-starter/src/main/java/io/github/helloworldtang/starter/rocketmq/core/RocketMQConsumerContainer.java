package io.github.helloworldtang.starter.rocketmq.core;

import io.github.helloworldtang.starter.rocketmq.annotation.RocketMQListener;
import io.github.helloworldtang.starter.rocketmq.config.RocketMQProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RocketMQConsumerContainer implements SmartInitializingSingleton, DisposableBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RocketMQConsumerContainer.class);

    private ApplicationContext applicationContext;
    private final RocketMQProperties properties;
    private final List<DefaultMQPushConsumer> consumers = new ArrayList<>();

    public RocketMQConsumerContainer(RocketMQProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RocketMQListener.class);
        beans.forEach(this::registerConsumer);
    }

    private void registerConsumer(String beanName, Object bean) {
        Class<?> targetClass = bean.getClass();
        RocketMQListener annotation = targetClass.getAnnotation(RocketMQListener.class);
        
        if (!(bean instanceof RocketMQMessageListener)) {
            throw new IllegalStateException("Bean " + beanName + " annotated with @RocketMQListener must implement RocketMQMessageListener");
        }

        RocketMQMessageListener listener = (RocketMQMessageListener) bean;
        
        String instanceId = annotation.instanceId();
        RocketMQProperties.InstanceConfig instanceConfig = properties.getInstances().get(instanceId);
        if (instanceConfig == null) {
            throw new IllegalStateException("RocketMQ instance config not found for id: " + instanceId);
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(annotation.consumerGroupId());
        consumer.setNamesrvAddr(instanceConfig.getNameServer());
        consumer.setConsumeThreadMin(annotation.consumeThreadNums());
        consumer.setConsumeThreadMax(annotation.consumeThreadNums());
        consumer.setMaxReconsumeTimes(annotation.maxReconsumeTimes());
        consumer.setConsumeTimeout(annotation.consumeTimeoutInMinutes());
        consumer.setMessageModel(annotation.messageModel());
        consumer.setPullBatchSize(annotation.pullBatchSize());
        consumer.setConsumeMessageBatchMaxSize(annotation.consumeMessageBatchMaxSize());
        // Handling accessKey/secretKey if needed via RPCHook or AclClient, omitting for simplicity unless requested.
        // Usually handled via creating a custom RPCHook if ACL is enabled.

        try {
            consumer.subscribe(annotation.topic(), annotation.tags());
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt msg : msgs) {
                        try {
                            log.info("Received message. Instance: {}, GroupId: {}, Topic: {}, Tag: {}, MsgId: {}, Body: {}",
                                    instanceId, annotation.consumerGroupId(), msg.getTopic(), msg.getTags(), msg.getMsgId(), new String(msg.getBody()));
                            listener.onMessage(msg);
                            log.info("Consumed message successfully. Instance: {}, GroupId: {}, Topic: {}, Tag: {}, MsgId: {}",
                                    instanceId, annotation.consumerGroupId(), msg.getTopic(), msg.getTags(), msg.getMsgId());
                        } catch (Exception e) {
                            log.error("Consume message failed. Instance: {}, GroupId: {}, Topic: {}, Tag: {}, MsgId: {}, Error: {}",
                                    instanceId, annotation.consumerGroupId(), msg.getTopic(), msg.getTags(), msg.getMsgId(), e.getMessage(), e);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
            consumers.add(consumer);
            log.info("Started RocketMQ consumer for bean: {}, instance: {}, topic: {}", beanName, instanceId, annotation.topic());
        } catch (MQClientException e) {
            throw new RuntimeException("Failed to start RocketMQ consumer for bean " + beanName, e);
        }
    }

    @Override
    public void destroy() {
        for (DefaultMQPushConsumer consumer : consumers) {
            try {
                consumer.shutdown();
                log.info("Shutdown RocketMQ consumer group: {}", consumer.getConsumerGroup());
            } catch (Exception e) {
                log.error("Failed to shutdown RocketMQ consumer", e);
            }
        }
    }
}
