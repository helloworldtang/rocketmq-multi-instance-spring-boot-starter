package io.github.helloworldtang.starter.rocketmq.annotation;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQListener {

    /**
     * Instance ID referring to the configuration key in application properties.
     * e.g. rocketmq.instances.{instanceId}
     */
    String instanceId();

    /**
     * Topic to subscribe to.
     */
    String topic();

    /**
     * Tags to subscribe to.
     * Default is "*" (all tags).
     * Multiple tags can be separated by "||".
     */
    String tags() default "*";

    /**
     * Consumer Group ID.
     */
    String consumerGroupId();

    /**
     * Message model: CLUSTERING or BROADCASTING.
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * Consumer thread count.
     */
    int consumeThreadNums() default 4;

    /**
     * Max re-consume times.
     */
    int maxReconsumeTimes() default 16;

    /**
     * Consume timeout in minutes.
     */
    int consumeTimeoutInMinutes() default 15;

    /**
     * Consumer ID (optional identifier).
     */
    String consumerId() default "";

    /**
     * Memo/Remarks.
     */
    String memo() default "";

    /**
     * Batch size for pulling messages.
     */
    int pullBatchSize() default 32;

    /**
     * Max messages to consume in a batch.
     */
    int consumeMessageBatchMaxSize() default 1;
}
