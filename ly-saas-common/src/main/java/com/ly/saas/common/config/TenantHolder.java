package com.ly.saas.common.config;

/**
 * @author linyuxb
 * @version 1.0
 * @date 2025/9/26 13:58
 * @description
 */
public class TenantHolder {
    private static final ThreadLocal<String> tenantLocal = new InheritableThreadLocal<>();

    public static void setTenantCode(String tenantCode) {
        if (!tenantCode.isBlank()) {
            tenantLocal.set(tenantCode);
        }
    }

    public static void remove() {
        tenantLocal.remove();
    }

    public static String getTenantCode() {
        return tenantLocal.get();
    }
}
