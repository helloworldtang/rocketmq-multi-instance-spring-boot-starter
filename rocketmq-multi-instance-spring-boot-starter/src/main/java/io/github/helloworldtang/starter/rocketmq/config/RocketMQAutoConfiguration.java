package io.github.helloworldtang.starter.rocketmq.config;

import io.github.helloworldtang.starter.rocketmq.core.RocketMQConsumerContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@Import(RocketMQProducerRegistrar.class)
public class RocketMQAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RocketMQConsumerContainer rocketMQConsumerContainer(RocketMQProperties properties) {
        return new RocketMQConsumerContainer(properties);
    }
}
