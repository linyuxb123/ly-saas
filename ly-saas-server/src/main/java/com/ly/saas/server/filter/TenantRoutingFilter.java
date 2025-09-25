
package com.ly.saas.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * @author linyuxb
 * @version 1.0
 * @date 2025/9/25 00:30
 * @description
 */
@Component
@Order(1)
public class TenantRoutingFilter extends OncePerRequestFilter {

    // 修正注解语法，使用正确的 SpEL 表达式
    @Value("#{${tenant.mapping}}")
    private Map<String, String> tenantMapping;

    @Value("${tenant.default-environment:wei}")
    private String defaultEnvironment;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取租户信息
        String tenant = request.getHeader("X-Tenant");

        // 根据租户获取对应环境，如果未配置则使用默认环境
        String environment = getEnvironmentForTenant(tenant);

        // 如果是API请求，则修改请求路径
        if (environment != null && request.getRequestURI().startsWith("/api/")) {
            // 包装请求以修改URI
            TenantAwareRequest wrappedRequest = new TenantAwareRequest(request, environment);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            // 不需要路由修改的情况
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 根据租户获取对应的环境
     * @param tenant 租户名称
     * @return 对应的环境，如果未找到则返回默认环境
     */
    private String getEnvironmentForTenant(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return defaultEnvironment;
        }

        // 根据配置映射获取环境
        String environment = tenantMapping.get(tenant);
        return environment != null ? environment : defaultEnvironment;
    }

    /**
     * 自定义请求包装类，用于修改请求URI
     */
    private static class TenantAwareRequest extends HttpServletRequestWrapper {

        private final String environment;
        private final String originalUri;

        public TenantAwareRequest(HttpServletRequest request, String environment) {
            super(request);
            this.environment = environment;
            this.originalUri = request.getRequestURI();
        }

        @Override
        public String getRequestURI() {
            // 如果是API请求且不包含环境前缀，则添加环境前缀
            if (originalUri.startsWith("/api/") && !originalUri.startsWith("/api/" + environment + "/")) {
                return "/api/" + environment + originalUri.substring(4);
            }
            return originalUri;
        }

        @Override
        public StringBuffer getRequestURL() {
            StringBuffer url = new StringBuffer();
            url.append(getScheme()).append("://").append(getServerName());

            int port = getServerPort();
            if (port != 80 && port != 443) {
                url.append(":").append(port);
            }

            url.append(getRequestURI());
            return url;
        }
    }
}