package io.github.helloworldtang.starter.rocketmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {

    private Map<String, InstanceConfig> instances = new HashMap<>();

    public Map<String, InstanceConfig> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, InstanceConfig> instances) {
        this.instances = instances;
    }

    public static class InstanceConfig {
        private String nameServer;
        private String accessKey;
        private String secretKey;
        private boolean enableTrace = false;
        private String traceTopic;

        public String getNameServer() {
            return nameServer;
        }

        public void setNameServer(String nameServer) {
            this.nameServer = nameServer;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public boolean isEnableTrace() {
            return enableTrace;
        }

        public void setEnableTrace(boolean enableTrace) {
            this.enableTrace = enableTrace;
        }

        public String getTraceTopic() {
            return traceTopic;
        }

        public void setTraceTopic(String traceTopic) {
            this.traceTopic = traceTopic;
        }
    }
}
