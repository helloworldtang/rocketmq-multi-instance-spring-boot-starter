package io.github.helloworldtang.starter.rocketmq.annotation;

import io.github.helloworldtang.starter.rocketmq.core.RocketMQMessageListener;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RocketMQListenerTest {

    @Test
    void testAnnotationDefaultValues() {
        @RocketMQListener(
            instanceId = "test",
            topic = "TestTopic",
            consumerGroupId = "test-group"
        )
        class TestListener implements RocketMQMessageListener {
            @Override
            public void onMessage(org.apache.rocketmq.common.message.MessageExt message) {
            }
        }

        RocketMQListener annotation = TestListener.class.getAnnotation(RocketMQListener.class);
        assertNotNull(annotation);
        assertEquals("test", annotation.instanceId());
        assertEquals("TestTopic", annotation.topic());
        assertEquals("test-group", annotation.consumerGroupId());
        assertEquals("*", annotation.tags());
        assertEquals(MessageModel.CLUSTERING, annotation.messageModel());
        assertEquals(4, annotation.consumeThreadNums());
        assertEquals(16, annotation.maxReconsumeTimes());
        assertEquals(15, annotation.consumeTimeoutInMinutes());
        assertEquals("", annotation.consumerId());
        assertEquals("", annotation.memo());
        assertEquals(32, annotation.pullBatchSize());
        assertEquals(1, annotation.consumeMessageBatchMaxSize());
    }

    @Test
    void testAnnotationCustomValues() {
        @RocketMQListener(
            instanceId = "custom",
            topic = "CustomTopic",
            tags = "tag1||tag2",
            consumerGroupId = "custom-group",
            messageModel = MessageModel.BROADCASTING,
            consumeThreadNums = 8,
            maxReconsumeTimes = 10,
            consumeTimeoutInMinutes = 20,
            consumerId = "consumer1",
            memo = "Test consumer",
            pullBatchSize = 64,
            consumeMessageBatchMaxSize = 5
        )
        class TestListener implements RocketMQMessageListener {
            @Override
            public void onMessage(org.apache.rocketmq.common.message.MessageExt message) {
            }
        }

        RocketMQListener annotation = TestListener.class.getAnnotation(RocketMQListener.class);
        assertNotNull(annotation);
        assertEquals("custom", annotation.instanceId());
        assertEquals("CustomTopic", annotation.topic());
        assertEquals("tag1||tag2", annotation.tags());
        assertEquals("custom-group", annotation.consumerGroupId());
        assertEquals(MessageModel.BROADCASTING, annotation.messageModel());
        assertEquals(8, annotation.consumeThreadNums());
        assertEquals(10, annotation.maxReconsumeTimes());
        assertEquals(20, annotation.consumeTimeoutInMinutes());
        assertEquals("consumer1", annotation.consumerId());
        assertEquals("Test consumer", annotation.memo());
        assertEquals(64, annotation.pullBatchSize());
        assertEquals(5, annotation.consumeMessageBatchMaxSize());
    }
}
