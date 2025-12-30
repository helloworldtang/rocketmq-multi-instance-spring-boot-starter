package io.github.helloworldtang.starter.rocketmq.config;

import io.github.helloworldtang.starter.rocketmq.core.RocketMQProducerContainer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;
import java.util.Map;

public class RocketMQProducerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Binder binder = Binder.get(environment);
        Map<String, RocketMQProperties.InstanceConfig> instances = binder.bind("rocketmq.instances", 
                Bindable.mapOf(String.class, RocketMQProperties.InstanceConfig.class)).orElse(Collections.emptyMap());

        instances.forEach((name, config) -> {
            String beanName = name + "Producer";
            if (!registry.containsBeanDefinition(beanName)) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DefaultMQProducer.class);
                builder.addConstructorArgValue(name + "-producer-group");
                builder.addPropertyValue("namesrvAddr", config.getNameServer());
                builder.setInitMethodName("start");
                builder.setDestroyMethodName("shutdown");
                registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
            }
        });
        
        // Register the container bean
        if (!registry.containsBeanDefinition(RocketMQProducerContainer.class.getName())) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RocketMQProducerContainer.class);
            registry.registerBeanDefinition(RocketMQProducerContainer.class.getName(), builder.getBeanDefinition());
        }
    }
}
