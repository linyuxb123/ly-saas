package com.ly.saas.server.filter;

import com.ly.saas.common.config.TenantHolder;
import com.ly.saas.common.config.TenantProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author linyuxb
 * @version 1.0
 * @date 2025/9/25 00:30
 * @description
 */
@Order(1)
@Component
public class TenantRoutingFilter extends OncePerRequestFilter {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private static final Logger log = LoggerFactory.getLogger(TenantRoutingFilter.class);

    @Autowired
    private TenantProperties tenantProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取租户信息
        String tenant = request.getHeader("X-Tenant");

        // 设置当前租户ID到UserDetailsService
        if (tenant != null && !tenant.isEmpty()) {
            TenantHolder.setTenantCode(tenant);
        }

        // 根据租户获取对应环境，如果未配置则使用默认环境
        String environment = tenantProperties.getEnvironmentForTenant(tenant);

        // 如果是API请求，则修改请求路径
        if (environment != null && request.getRequestURI().startsWith(contextPath) &&
                !request.getRequestURI().startsWith(contextPath + "/saas-server")) {
            // 包装请求以修改URI
            TenantAwareRequest wrappedRequest = new TenantAwareRequest(request, environment);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            // 不需要路由修改的情况
            filterChain.doFilter(request, response);
        }
        TenantHolder.remove();
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