package io.github.helloworldtang.starter.rocketmq.core;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class RocketMQProducerContainerTest {

    private AnnotationConfigApplicationContext context;
    private RocketMQProducerContainer container;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.registerBean(RocketMQProducerContainer.class);
        context.refresh();
        container = context.getBean(RocketMQProducerContainer.class);
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void testGetProducerBeforeInitialization() {
        // Create a new container without initializing applicationContext
        RocketMQProducerContainer newContainer = new RocketMQProducerContainer();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            newContainer.getProducer("test");
        });
        assertTrue(exception.getMessage().contains("has not been initialized"));
    }

    @Test
    void testGetProducerWithNonExistentBean() {
        container.setApplicationContext(context);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            container.getProducer("nonExistent");
        });
        assertTrue(exception.getMessage().contains("No producer found"));
    }

    @Test
    void testGetProducersReturnsEmptyMap() {
        container.setApplicationContext(context);
        assertTrue(container.getProducers().isEmpty());
    }

    @Test
    void testGetProducersReturnsUnmodifiableMap() {
        container.setApplicationContext(context);
        java.util.Map<String, org.apache.rocketmq.client.producer.DefaultMQProducer> producers = container.getProducers();
        assertThrows(UnsupportedOperationException.class, () -> {
            producers.put("test", null);
        });
    }
}
