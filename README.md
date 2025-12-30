# RocketMQ Multi-Instance Spring Boot Starter

## 简介

这是一个用于 Spring Boot 应用接入 RocketMQ 的 Starter 组件。它旨在解决单一应用需要连接多个 RocketMQ 集群或实例的场景，并提供声明式的消息消费能力，简化了 RocketMQ 的集成过程。

主要解决的问题：

1. **多实例支持**：允许在一个应用中配置和连接多个不同的 RocketMQ Nameserver/Broker。
2. **声明式消费**：通过注解`@RocketMQListener`即可完成消费者的定义和注册，无需编写繁琐的样板代码。
3. **兼容性**：同时支持 Spring Boot 2.x 和 3.x 版本，基于 JDK 1.8+。

**最新版本**：0.1.0

## 快速开始

### 1. 引入依赖

在你的 `pom.xml` 中添加如下依赖：

```xml
<dependency>
    <groupId>io.github.helloworldtang</groupId>
    <artifactId>rocketmq-multi-instance-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 或 `application.properties` 中配置 RocketMQ 实例信息：

```yaml
rocketmq:
  instances:
    instance1:
      name-server: 127.0.0.1:9876
      # access-key: your-access-key (可选)
      # secret-key: your-secret-key (可选)
    instance2:
      name-server: 192.168.1.100:9876
```

### 3. 发送消息

组件提供了灵活的方式获取 `DefaultMQProducer` 实例。

#### 方式一：静态工具类（推荐）

在任何地方（包括非 Spring 管理的类）均可使用：

```java
// 直接获取指定实例的 Producer
DefaultMQProducer producer = RocketMQProducerContainer.get("instance1");
if (producer != null) {
    producer.send(new Message("TopicTest", "TagA", "Hello World".getBytes()));
}
```

#### 方式二：Spring 注入

使用 `@Qualifier` 指定 Bean 名称，Bean 名称规则为 `{instanceId}Producer`：

```java
@RestController
public class ProducerController {

    @Autowired
    @Qualifier("instance1Producer")
    private DefaultMQProducer producer;

    @GetMapping("/send")
    public String send(@RequestParam("msg") String msg) {
        try {
            Message message = new Message("TopicTest", "TagA", msg.getBytes());
            producer.send(message);
            return "Sent: " + msg;
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }
}
```

### 4. 消费消息

实现 `RocketMQMessageListener` 接口，并在类上添加 `@RocketMQListener` 注解。

```java
@Component
@RocketMQListener(
        instanceId = "instance1",       // 指定使用哪个实例配置
        topic = "TopicTest",            // 订阅的主题
        consumerGroupId = "my-group",   // 消费者组ID
        tags = "*"                      // 订阅的Tag，默认*
)
public class MyConsumer implements RocketMQMessageListener {

    @Override
    public void onMessage(MessageExt message) {
        System.out.println("Received message: " + new String(message.getBody()));
        // 如果抛出异常，消息将会在稍后重试
    }
}
```

## 验证与验收

本项目包含一个 `rocketmq-demo` 模块，用于演示和验证功能。

1. **环境准备**：

   - 启动本地 RocketMQ NameServer (默认端口 9876) 和 Broker (默认端口 10911)。
   - 确保 NameServer 地址为 `127.0.0.1:9876` (或修改 demo 的 application.yml)。

2. **启动应用**：

   - 运行 `rocketmq-demo` 模块下的 `DemoApplication`。
   - 观察日志，确认 `RocketMQConsumerContainer` 成功启动并注册了消费者。

3. **功能验证**：
   - **发送消息**：
     访问 `http://localhost:8081/send?msg=Hello`
     浏览器应返回 `Sent: Hello`。
   - **消费消息**：
     查看控制台日志，应能看到如下格式的日志：
     ```text
     Received message. Instance: instance1, GroupId: my-group, Topic: TopicTest, Tag: TagA, MsgId: ..., Body: Hello
     Consumed message successfully. ...
     ```

## 技术亮点

- **无侵入设计**：基于 Spring Boot 自动配置机制，引入 Starter 即可使用，不破坏原有项目结构。
- **多实例隔离**：通过 `instanceId` 逻辑隔离不同集群的配置和客户端实例，互不干扰。
- **优雅的 API**：
  - 提供 `@RocketMQListener` 注解，屏蔽了底层消费者创建、订阅、监听器注册等复杂细节。
  - 提供 `RocketMQProducerContainer` 静态访问入口，解决了在 Utils 等非 Spring Bean 中获取 Producer 的痛点。
- **健壮性**：
  - **自动重试**：消费异常自动返回 `RECONSUME_LATER`。
  - **全链路日志**：内置详细的收发消息日志，包含 GroupId、MsgId 等关键信息，便于排查问题。
- **高兼容性**：
  - **Spring Boot**: 同时兼容 2.x 和 3.x。
  - **JDK**: 兼容 JDK 8 及以上版本。

## 技术细节

### 核心注解 @RocketMQListener

该注解用于标记消息消费者，支持以下属性配置：

- `instanceId`: 关联配置中的实例 ID。
- `topic`: 订阅的主题。
- `tags`: 订阅的标签，支持 `||` 分割。
- `consumerGroupId`: 消费者组 ID。
- `messageModel`: 消息模式，支持 `CLUSTERING` (默认) 和 `BROADCASTING`。
- `consumeThreadNums`: 消费线程数，默认 4。
- `maxReconsumeTimes`: 最大重试次数，默认 16。
- `consumeTimeoutInMinutes`: 消费超时时间，默认 15 分钟。

### 兼容性说明

- **Spring Boot**: 通过 `spring.factories` (Spring Boot 2.x) 和 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 3.x) 实现了跨版本的自动配置兼容。
- **RocketMQ**: 默认依赖 RocketMQ Client 4.9.4，兼容 RocketMQ 4.x/5.x (4.x 协议模式) 服务端。
- **JDK**: 最低要求 1.8。

## License

Apache License 2.0
