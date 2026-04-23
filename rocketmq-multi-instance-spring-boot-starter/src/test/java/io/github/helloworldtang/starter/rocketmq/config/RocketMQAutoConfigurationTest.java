package io.github.helloworldtang.starter.rocketmq.config;

import io.github.helloworldtang.starter.rocketmq.core.RocketMQConsumerContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RocketMQAutoConfiguration.class)
@TestPropertySource(properties = {
    "rocketmq.instances.instance1.name-server=127.0.0.1:9876"
})
class RocketMQAutoConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RocketMQConsumerContainer consumerContainer;

    @Test
    void testAutoConfigurationLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void testRocketMQPropertiesBeanExists() {
        RocketMQProperties properties = applicationContext.getBean(RocketMQProperties.class);
        assertNotNull(properties);
    }

    @Test
    void testRocketMQConsumerContainerBeanExists() {
        assertNotNull(consumerContainer);
        assertTrue(applicationContext.containsBean("rocketMQConsumerContainer"));
    }
}
