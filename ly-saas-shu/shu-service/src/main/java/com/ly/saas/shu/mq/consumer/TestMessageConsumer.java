package com.ly.saas.shu.mq.consumer;

import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.mq.model.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 测试消息消费者
 */
@Slf4j
@Component(Constants.PREFIX + "TestMessageConsumer")
@RocketMQMessageListener(topic = Constants.PREFIX + "${rocketmq.topic.test}",
        consumerGroup = Constants.PREFIX + "${rocketmq.consumer.group}",
        nameServer = "${rocketmq.name-server}")
public class TestMessageConsumer implements RocketMQListener<TestMessage> {

    @Override
    public void onMessage(TestMessage message) {
        try {
            log.info("{}, 接收到消息: {}", Constants.PREFIX, message);
            // 处理消息
            processMessage(message);
            log.info("{}, 消息处理完成: {}", Constants.PREFIX, message.getId());
        } catch (Exception e) {
            log.error("{}, 消息处理异常: {}", Constants.PREFIX, message, e);
        }
    }

    /**
     * 处理消息
     *
     * @param message 消息
     */
    private void processMessage(TestMessage message) {
        // 模拟消息处理
        log.info("{}, 正在处理消息: ID={}, 内容={}, 发送时间={}",
                Constants.PREFIX, message.getId(), message.getContent(), message.getSendTime());

        // 这里可以添加实际的业务逻辑处理
        try {
            // 模拟处理耗时
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("{}, 消息处理被中断", Constants.PREFIX, e);
        }
    }
}