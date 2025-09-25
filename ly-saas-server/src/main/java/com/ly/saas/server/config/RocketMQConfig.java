package com.ly.saas.server.config;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * RocketMQ 配置类
 * 手动导入 RocketMQ 的自动配置类，确保 RocketMQTemplate 等 Bean 被正确创建
 */
@Configuration
@Import(RocketMQAutoConfiguration.class)
public class RocketMQConfig {
    // 无需额外配置，通过导入 RocketMQAutoConfiguration 自动配置类即可
}