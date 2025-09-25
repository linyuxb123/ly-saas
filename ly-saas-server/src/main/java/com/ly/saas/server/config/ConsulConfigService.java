package com.ly.saas.server.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Consul配置服务类
 * 用于管理Consul配置中心的操作
 */
@Service
@EnableScheduling
public class ConsulConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConsulConfigService.class);
    private static final String TENANT_MAPPING_KEY = "config/ly-saas/tenant-mapping";
    private static final String DEFAULT_ENV_KEY = "config/ly-saas/default-environment";

    @Autowired
    private ConsulClient consulClient;

    @Autowired
    private ConsulConfigProperties consulConfigProperties;

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 初始化方法，将本地配置迁移到Consul
     */
    @PostConstruct
    public void init() {
        if (tenantProperties.getConfig().isEnabled()) {
            try {
                // 检查Consul中是否已存在配置
                if (!isConfigExistsInConsul()) {
                    log.info("Consul中不存在租户映射配置，开始迁移本地配置到Consul");
                    // 迁移租户映射配置到Consul
                    migrateTenantMappingToConsul();
                    log.info("租户映射配置已成功迁移到Consul");
                } else {
                    log.info("Consul中已存在租户映射配置，跳过迁移");
                    // 从Consul加载配置
                    loadConfigFromConsul();
                }
            } catch (Exception e) {
                log.error("初始化Consul配置失败", e);
            }
        }
    }

    /**
     * 检查Consul中是否已存在配置
     */
    private boolean isConfigExistsInConsul() {
        try {
            Response<GetValue> response = consulClient.getKVValue(TENANT_MAPPING_KEY);
            return response != null && response.getValue() != null;
        } catch (Exception e) {
            log.error("检查Consul配置失败", e);
            return false;
        }
    }

    /**
     * 迁移租户映射配置到Consul
     */
    public void migrateTenantMappingToConsul() {
        try {
            // 将租户映射配置写入Consul
            String mappingJson = objectMapper.writeValueAsString(tenantProperties.getMapping());
            consulClient.setKVValue(TENANT_MAPPING_KEY, mappingJson);

            // 将默认环境配置写入Consul
            consulClient.setKVValue(DEFAULT_ENV_KEY, tenantProperties.getDefaultEnvironment());
        } catch (JsonProcessingException e) {
            log.error("序列化租户映射配置失败", e);
        }
    }

    /**
     * 从Consul加载配置
     */
    @SuppressWarnings("unchecked")
    public void loadConfigFromConsul() {
        try {
            // 从Consul获取租户映射配置
            Response<GetValue> mappingResponse = consulClient.getKVValue(TENANT_MAPPING_KEY);
            if (mappingResponse != null && mappingResponse.getValue() != null) {
                String mappingJson = decodeConsulValue(mappingResponse.getValue().getValue());
                if (StringUtils.hasText(mappingJson)) {
                    Map<String, String> mapping = objectMapper.readValue(mappingJson, HashMap.class);
                    tenantProperties.setMapping(mapping);
                    log.debug("从Consul加载租户映射配置: {}", mapping);
                }
            }

            // 从Consul获取默认环境配置
            Response<GetValue> defaultEnvResponse = consulClient.getKVValue(DEFAULT_ENV_KEY);
            if (defaultEnvResponse != null && defaultEnvResponse.getValue() != null) {
                String defaultEnv = decodeConsulValue(defaultEnvResponse.getValue().getValue());
                if (StringUtils.hasText(defaultEnv)) {
                    tenantProperties.setDefaultEnvironment(defaultEnv);
                    log.debug("从Consul加载默认环境配置: {}", defaultEnv);
                }
            }
        } catch (Exception e) {
            log.error("从Consul加载配置失败", e);
        }
    }

    /**
     * 更新租户映射配置
     *
     * @param tenantId    租户ID
     * @param environment 环境
     */
    public void updateTenantMapping(String tenantId, String environment) {
        try {
            // 获取当前映射
            Map<String, String> mapping = new HashMap<>(tenantProperties.getMapping());

            // 更新映射
            mapping.put(tenantId, environment);

            // 更新本地配置
            tenantProperties.setMapping(mapping);

            // 更新Consul配置
            String mappingJson = objectMapper.writeValueAsString(mapping);
            consulClient.setKVValue(TENANT_MAPPING_KEY, mappingJson);

            log.info("租户映射配置已更新: {} -> {}", tenantId, environment);
        } catch (JsonProcessingException e) {
            log.error("更新租户映射配置失败", e);
        }
    }

    /**
     * 更新默认环境配置
     *
     * @param defaultEnvironment 默认环境
     */
    public void updateDefaultEnvironment(String defaultEnvironment) {
        // 更新本地配置
        tenantProperties.setDefaultEnvironment(defaultEnvironment);

        // 更新Consul配置
        consulClient.setKVValue(DEFAULT_ENV_KEY, defaultEnvironment);

        log.info("默认环境配置已更新: {}", defaultEnvironment);
    }

    /**
     * 定时刷新配置
     * 根据配置的刷新间隔从Consul加载配置
     */
    @Scheduled(fixedDelayString = "${tenant.config.refresh-interval:10}000")
    public void refreshConfig() {
        if (tenantProperties.getConfig().isEnabled()) {
            log.debug("定时刷新Consul配置");
            loadConfigFromConsul();
        }
    }

    /**
     * 解码Consul值
     *
     * @param value Base64编码的值
     * @return 解码后的字符串
     */
    private String decodeConsulValue(String value) {
        if (value == null) {
            return null;
        }
        byte[] str = new byte[0];
        if (!value.isEmpty()) {
            str = Base64.getDecoder().decode(value);
        }
        return new String(str, StandardCharsets.UTF_8);
    }
}