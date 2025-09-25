package com.ly.saas.shu.security;

import com.ly.saas.shu.core.entity.User;
import com.ly.saas.shu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ly.saas.shu.core.constant.Constants;

/**
 * 自定义 UserDetailsService 实现类
 */
@Service(Constants.PREFIX + "SaasUserDetailsService")
public class SaaSUserDetailsService implements UserDetailsService {

    private UserService userService;

    private String currentTenantId;

    @Lazy
    @Autowired
    public void setUserService(@Qualifier(Constants.PREFIX + "UserService") UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户是否属于当前租户
        if (currentTenantId != null && !currentTenantId.equals(user.getTenantId())) {
            throw new UsernameNotFoundException("用户不属于当前租户");
        }

        // 加载用户角色和权限
        user = userService.getUserWithRolesAndPermissions(user.getId());

        return new SaaSUserDetails(user, currentTenantId);
    }

    public void setCurrentTenantId(String tenantId) {
        this.currentTenantId = tenantId;
    }

    public String getCurrentTenantId() {
        return currentTenantId;
    }
}