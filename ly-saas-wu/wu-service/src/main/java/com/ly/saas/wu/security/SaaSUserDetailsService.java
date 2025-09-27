package com.ly.saas.wu.security;

import com.ly.saas.common.config.TenantHolder;
import com.ly.saas.wu.core.entity.User;
import com.ly.saas.wu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ly.saas.wu.core.constant.Constants;

import java.util.Objects;

/**
 * 自定义 UserDetailsService 实现类
 */
@Slf4j
@Service(Constants.PREFIX + "SaasUserDetailsService")
public class SaaSUserDetailsService implements UserDetailsService {

    private UserService userService;

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
        if (!Objects.equals(TenantHolder.getTenantCode(), user.getTenantId())) {
            log.warn("用户不属于当前租户: {}，{}，{}", TenantHolder.getTenantCode(), user.getTenantId(), username);
            throw new UsernameNotFoundException("用户不属于当前租户");
        }

        // 加载用户角色和权限
        user = userService.getUserWithRolesAndPermissions(user.getId());

        return new SaaSUserDetails(user, user.getTenantId());
    }

}