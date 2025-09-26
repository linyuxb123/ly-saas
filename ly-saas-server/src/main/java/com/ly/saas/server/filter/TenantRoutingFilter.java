package com.ly.saas.server.filter;

import com.ly.saas.common.config.TenantHolder;
import com.ly.saas.common.config.TenantProperties;
import com.ly.saas.server.constant.Constants;
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
                // 排除API请求，因为API请求已经包含环境前缀
                !request.getRequestURI().startsWith(contextPath + Constants.API_PREFIX) &&
                !request.getRequestURI().startsWith(contextPath + com.ly.saas.wei.core.constant.Constants.API_PREFIX) &&
                !request.getRequestURI().startsWith(contextPath + com.ly.saas.shu.core.constant.Constants.API_PREFIX) &&
                !request.getRequestURI().startsWith(contextPath + com.ly.saas.wu.core.constant.Constants.API_PREFIX)
        ) {
            // 包装请求以修改URI
            TenantAwareRequest wrappedRequest = new TenantAwareRequest(contextPath, request, environment);
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
        private final String contextPath;

        public TenantAwareRequest(String contextPath, HttpServletRequest request, String environment) {
            super(request);
            this.environment = environment;
            this.originalUri = request.getRequestURI();
            this.contextPath = contextPath;
        }

        @Override
        public String getRequestURI() {
            // 如果是API请求且不包含环境前缀，则添加环境前缀
            if (originalUri.startsWith(contextPath + "/") && !originalUri.startsWith(contextPath + "/" + environment + "/")) {
                return contextPath + "/" + environment + originalUri.substring(contextPath.length());
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