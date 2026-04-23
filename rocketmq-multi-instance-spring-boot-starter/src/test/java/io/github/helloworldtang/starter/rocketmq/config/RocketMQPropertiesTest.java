package io.github.helloworldtang.starter.rocketmq.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RocketMQPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
    "rocketmq.instances.instance1.name-server=127.0.0.1:9876",
    "rocketmq.instances.instance1.access-key=accessKey1",
    "rocketmq.instances.instance1.secret-key=secretKey1",
    "rocketmq.instances.instance1.enable-trace=true",
    "rocketmq.instances.instance1.trace-topic=TraceTopic",
    "rocketmq.instances.instance2.name-server=192.168.1.100:9876"
})
public class RocketMQPropertiesTest {

    @org.springframework.beans.factory.annotation.Autowired
    private RocketMQProperties properties;

    @EnableConfigurationProperties(RocketMQProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesLoading() {
        assertNotNull(properties);
        assertNotNull(properties.getInstances());
        assertEquals(2, properties.getInstances().size());
        assertTrue(properties.getInstances().containsKey("instance1"));
        assertTrue(properties.getInstances().containsKey("instance2"));
    }

    @Test
    void testInstance1Config() {
        RocketMQProperties.InstanceConfig config = properties.getInstances().get("instance1");
        assertNotNull(config);
        assertEquals("127.0.0.1:9876", config.getNameServer());
        assertEquals("accessKey1", config.getAccessKey());
        assertEquals("secretKey1", config.getSecretKey());
        assertTrue(config.isEnableTrace());
        assertEquals("TraceTopic", config.getTraceTopic());
    }

    @Test
    void testInstance2Config() {
        RocketMQProperties.InstanceConfig config = properties.getInstances().get("instance2");
        assertNotNull(config);
        assertEquals("192.168.1.100:9876", config.getNameServer());
    }

    @Test
    void testInstanceConfigDefaults() {
        RocketMQProperties.InstanceConfig config = properties.getInstances().get("instance2");
        assertNotNull(config);
        assertEquals(null, config.getAccessKey());
        assertEquals(null, config.getSecretKey());
        assertEquals(false, config.isEnableTrace());
        assertEquals(null, config.getTraceTopic());
    }
}
