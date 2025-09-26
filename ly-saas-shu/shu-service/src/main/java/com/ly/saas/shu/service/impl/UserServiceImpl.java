package com.ly.saas.shu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.core.entity.User;
import com.ly.saas.shu.core.mapper.UserMapper;
import com.ly.saas.shu.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service(Constants.PREFIX + "UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    public void setPasswordEncoder(@Qualifier(Constants.PREFIX + "PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // 创建默认管理员用户
        String adminUsername = "admin";
        String adminPassword = "admin123";
        String adminTenantId = "huawei";

        if (getByUsernameAndTenantId(adminUsername, adminTenantId) == null) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRealName("系统管理员");
            admin.setStatus(1);
            admin.setTenantId(adminTenantId);

            save(admin);
            log.info("创建默认管理员用户: {}", adminUsername);
        }
    }

    @Override
    public User getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return getOne(queryWrapper);
    }

    @Override
    public Page<User> pageUsers(Page<User> page, User user) {
        log.info(Constants.PREFIX + "获取用户列表，page：{}, user：{}", page, user);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (user != null) {
            // 根据用户名模糊查询
            if (StringUtils.hasText(user.getUsername())) {
                queryWrapper.like(User::getUsername, user.getUsername());
            }
            // 根据真实姓名模糊查询
            if (StringUtils.hasText(user.getRealName())) {
                queryWrapper.like(User::getRealName, user.getRealName());
            }
            // 根据部门ID查询
            if (user.getDeptId() != null) {
                queryWrapper.eq(User::getDeptId, user.getDeptId());
            }
            // 根据状态查询
            if (user.getStatus() != null) {
                queryWrapper.eq(User::getStatus, user.getStatus());
            }
        }
        return page(page, queryWrapper);
    }

    @Override
    public List<User> allUsers() {
        log.info(Constants.PREFIX + "获取所有用户列表");
        return super.getBaseMapper().allUsers();
    }

    @Override
    public List<User> listByDeptId(Long deptId) {
        if (deptId == null) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDeptId, deptId);
        return list(queryWrapper);
    }

    @Override
    public User getUserWithRolesAndPermissions(Long userId) {
        User user = getById(userId);
        if (user != null) {
            // 这里应该从数据库中查询用户的角色和权限
            // 由于示例中没有角色和权限表，这里模拟一些数据

            // 模拟角色数据
            List<String> roles = new ArrayList<>();
            if ("admin".equals(user.getUsername())) {
                roles.add("ADMIN");
            } else {
                roles.add("USER");
            }
            user.setRoles(roles);

            // 模拟权限数据
            List<String> permissions = new ArrayList<>();
            if ("admin".equals(user.getUsername())) {
                permissions.addAll(Arrays.asList("user:read", "user:write", "dept:read", "dept:write"));
            } else {
                permissions.addAll(Arrays.asList("user:read", "dept:read"));
            }
            user.setPermissions(permissions);
        }
        return user;
    }

    @Override
    public User getByUsernameAndTenantId(String username, String tenantId) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(tenantId)) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getTenantId, tenantId);
        return getOne(queryWrapper);
    }
}