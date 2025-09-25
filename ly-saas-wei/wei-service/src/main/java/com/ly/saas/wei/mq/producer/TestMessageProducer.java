package com.ly.saas.wei.mq.producer;

import com.ly.saas.wei.core.constant.Constants;
import com.ly.saas.wei.mq.model.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 测试消息生产者
 */
@Slf4j
@Component(Constants.PREFIX + "TestMessageProducer")
public class TestMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value(Constants.PREFIX + "${rocketmq.topic.test}")
    private String testTopic;

    /**
     * 同步发送消息
     *
     * @param content 消息内容
     * @return 发送结果
     */
    public SendResult sendMessage(String content) {
        TestMessage message = new TestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());

        log.info("{}，开始发送同步消息: {}", Constants.PREFIX, message);
        SendResult sendResult = rocketMQTemplate.syncSend(testTopic,
                MessageBuilder.withPayload(message).build());
        log.info("{}，同步消息发送结果: {}", Constants.PREFIX, sendResult);
        return sendResult;
    }

    /**
     * 异步发送消息
     *
     * @param content 消息内容
     */
    public void sendAsyncMessage(String content) {
        TestMessage message = new TestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());

        log.info("{}，开始发送异步消息: {}", Constants.PREFIX, message);
        rocketMQTemplate.asyncSend(testTopic,
                MessageBuilder.withPayload(message).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("{}，异步消息发送成功: {}", Constants.PREFIX, sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("{}，异步消息发送失败", Constants.PREFIX, throwable);
                    }
                });
    }

    /**
     * 单向发送消息（不关心发送结果）
     *
     * @param content 消息内容
     */
    public void sendOneWayMessage(String content) {
        TestMessage message = new TestMessage();
        message.setId(UUID.randomUUID().toString());
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());

        log.info("{}，开始发送单向消息: {}", Constants.PREFIX, message);
        rocketMQTemplate.sendOneWay(testTopic,
                MessageBuilder.withPayload(message).build());
        log.info("{}，单向消息发送完成", Constants.PREFIX);
    }
}