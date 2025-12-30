package io.github.helloworldtang.starter.rocketmq.core;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketMQProducerContainer implements ApplicationContextAware {

    private static RocketMQProducerContainer instance;
    private final Map<String, DefaultMQProducer> producerMap = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        instance = this;
    }

    /**
     * Get the producer by instanceId (Static convenience method).
     *
     * @param instanceId the instance id configured in properties
     * @return the DefaultMQProducer or null if not found
     */
    public static DefaultMQProducer get(String instanceId) {
        if (instance == null) {
            throw new IllegalStateException("RocketMQProducerContainer has not been initialized yet.");
        }
        return instance.getProducer(instanceId);
    }

    /**
     * Get the producer by instanceId.
     *
     * @param instanceId the instance id configured in properties
     * @return the DefaultMQProducer or null if not found
     */
    public DefaultMQProducer getProducer(String instanceId) {
        return producerMap.computeIfAbsent(instanceId, id -> {
            String beanName = id + "Producer";
            if (applicationContext.containsBean(beanName)) {
                return applicationContext.getBean(beanName, DefaultMQProducer.class);
            }
            return null;
        });
    }

    /**
     * Get all loaded producers.
     *
     * @return unmodifiable map of instanceId -> DefaultMQProducer
     */
    public Map<String, DefaultMQProducer> getProducers() {
        return Collections.unmodifiableMap(producerMap);
    }
}
