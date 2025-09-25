package com.ly.saas.shu.security;

import com.ly.saas.shu.core.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情类
 */
public class SaaSUserDetails implements UserDetails {

    private final User user;
    private final String tenantId;
    private final Collection<GrantedAuthority> authorities;

    public SaaSUserDetails(User user, String tenantId) {
        this.user = user;
        this.tenantId = tenantId;

        // 构建权限列表
        List<GrantedAuthority> auths = new ArrayList<>();

        // 添加角色
        if (user.getRoles() != null) {
            auths.addAll(user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList()));
        }

        // 添加权限
        if (user.getPermissions() != null) {
            auths.addAll(user.getPermissions().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }

        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }

    public User getUser() {
        return user;
    }

    public String getTenantId() {
        return tenantId;
    }
}