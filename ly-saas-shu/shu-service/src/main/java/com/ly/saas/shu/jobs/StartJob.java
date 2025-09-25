package com.ly.saas.shu.jobs;

import com.ly.saas.common.config.TenantProperties;
import com.ly.saas.shu.core.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author linyuxb
 * @version 1.0
 * @date 2025/9/25 19:10
 * @description
 */
@Slf4j
@Component("shuStartJob")
@EnableScheduling
public class StartJob {
    @Autowired
    private TenantProperties tenantProperties;

    @Scheduled(fixedDelay = 60 * 1000) // 每30秒执行一次
    public void processLogUpdates() {
        for (Map.Entry<String, String> stringStringEntry : tenantProperties.getMapping().entrySet()) {
            if (stringStringEntry.getValue().equals(Constants.PREFIX)) {
                log.info("{}，开始处理租户：{}", Constants.PREFIX, stringStringEntry.getKey());
                // 分布式锁，防止重复执行
            }
        }
        if (tenantProperties.getDefaultEnvironment().equals(Constants.PREFIX)) {
            log.info("{}，开始处理默认环境", Constants.PREFIX);
            // 分布式锁，防止重复执行
        }
    }
}