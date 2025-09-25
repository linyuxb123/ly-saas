package com.ly.saas.wu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.saas.wu.core.constant.Constants;
import com.ly.saas.wu.core.entity.User;
import com.ly.saas.wu.core.mapper.UserMapper;
import com.ly.saas.wu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service(Constants.PREFIX + "UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

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
}