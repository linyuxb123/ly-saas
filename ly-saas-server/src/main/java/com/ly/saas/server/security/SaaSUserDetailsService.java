package com.ly.saas.server.security;

import com.ly.saas.common.config.TenantHolder;
import com.ly.saas.common.config.TenantProperties;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义 UserDetailsService 实现类
 */
@Service("saasUserDetailsService")
public class SaaSUserDetailsService implements UserDetailsService {

    @Autowired
    private TenantProperties tenantProperties;

    @Resource(name = "shuSaasUserDetailsService")
    private com.ly.saas.shu.security.SaaSUserDetailsService shuSaasUserDetailsService;
    @Resource(name = "weiSaasUserDetailsService")
    private com.ly.saas.wei.security.SaaSUserDetailsService weiSaasUserDetailsService;
    @Resource(name = "wuSaasUserDetailsService")
    private com.ly.saas.wu.security.SaaSUserDetailsService wuSaasUserDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String env = tenantProperties.getEnvironmentForTenant(TenantHolder.getTenantCode());
        return switch (env) {
            case "shu" -> shuSaasUserDetailsService.loadUserByUsername(username);
            case "wei" -> weiSaasUserDetailsService.loadUserByUsername(username);
            case "wu" -> wuSaasUserDetailsService.loadUserByUsername(username);
            default -> null;
        };
    }
}
