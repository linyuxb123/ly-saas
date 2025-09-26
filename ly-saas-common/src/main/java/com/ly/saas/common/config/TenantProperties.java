package com.ly.saas.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置属性类
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "tenant")
@RefreshScope
public class TenantProperties {

    /**
     * 租户到环境的映射关系
     */
    private Map<String, String> mapping = new HashMap<>();

    /**
     * 默认环境
     */
    private String defaultEnvironment;

    /**
     * 配置相关属性
     */
    private Config config = new Config();

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 配置相关内部类
     */
    public static class Config {
        /**
         * 是否启用Consul配置
         */
        private boolean enabled = true;

        /**
         * 配置刷新间隔(秒)
         */
        private int refreshInterval = 10;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getRefreshInterval() {
            return refreshInterval;
        }

        public void setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
        }
    }

    /**
     * 根据租户获取对应的环境
     * @param tenant 租户名称
     * @return 对应的环境，如果未找到则返回默认环境
     */
    public String getEnvironmentForTenant(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return defaultEnvironment;
        }

        // 根据配置映射获取环境
        String environment = mapping.get(tenant);

        if (environment != null) {
            log.debug("租户 [{}] 映射到环境: {}", tenant, environment);
            return environment;
        } else {
            log.debug("租户 [{}] 未找到映射，使用默认环境: {}", tenant, defaultEnvironment);
            return defaultEnvironment;
        }
    }
}