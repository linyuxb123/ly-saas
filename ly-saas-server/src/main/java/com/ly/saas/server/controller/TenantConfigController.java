package com.ly.saas.server.controller;

import com.ly.saas.common.config.ConsulConfigService;
import com.ly.saas.common.config.TenantProperties;
import com.ly.saas.server.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置控制器
 * 提供租户环境映射配置的管理接口
 */
@RestController
@RequestMapping(Constants.API_PREFIX + "/tenant-config")
public class TenantConfigController {

    @Autowired
    private TenantProperties tenantProperties;

    @Autowired
    private ConsulConfigService consulConfigService;

    /**
     * 获取所有租户环境映射配置
     */
    @GetMapping("/mappings")
    public ResponseEntity<Map<String, Object>> getTenantMappings() {
        Map<String, Object> result = new HashMap<>();
        result.put("mappings", tenantProperties.getMapping());
        result.put("defaultEnvironment", tenantProperties.getDefaultEnvironment());
        return ResponseEntity.ok(result);
    }

    /**
     * 更新租户环境映射
     *
     * @param tenantId    租户ID
     * @param environment 环境
     */
    @PostMapping("/mapping")
    public ResponseEntity<Map<String, Object>> updateTenantMapping(
            @RequestParam String tenantId,
            @RequestParam String environment) {

        consulConfigService.updateTenantMapping(tenantId, environment);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "租户环境映射已更新");
        result.put("tenantId", tenantId);
        result.put("environment", environment);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除租户环境映射
     *
     * @param tenantId 租户ID
     */
    @DeleteMapping("/mapping/{tenantId}")
    public ResponseEntity<Map<String, Object>> deleteTenantMapping(
            @PathVariable String tenantId) {

        Map<String, String> mapping = new HashMap<>(tenantProperties.getMapping());
        if (mapping.containsKey(tenantId)) {
            mapping.remove(tenantId);

            // 更新本地配置
            tenantProperties.setMapping(mapping);

            // 更新Consul配置
            try {
                consulConfigService.migrateTenantMappingToConsul();

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "租户环境映射已删除");
                result.put("tenantId", tenantId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "删除租户环境映射失败: " + e.getMessage());
                return ResponseEntity.badRequest().body(result);
            }
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "租户不存在");
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新默认环境
     *
     * @param environment 默认环境
     */
    @PostMapping("/default-environment")
    public ResponseEntity<Map<String, Object>> updateDefaultEnvironment(
            @RequestParam String environment) {

        consulConfigService.updateDefaultEnvironment(environment);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "默认环境已更新");
        result.put("defaultEnvironment", environment);
        return ResponseEntity.ok(result);
    }

    /**
     * 刷新配置
     * 从Consul重新加载配置
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshConfig() {
        consulConfigService.loadConfigFromConsul();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "配置已刷新");
        result.put("mappings", tenantProperties.getMapping());
        result.put("defaultEnvironment", tenantProperties.getDefaultEnvironment());
        return ResponseEntity.ok(result);
    }
}