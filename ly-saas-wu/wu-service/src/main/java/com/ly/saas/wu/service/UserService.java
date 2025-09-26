package com.ly.saas.wu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.saas.wu.core.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param user 查询条件
     * @return 用户分页列表
     */
    Page<User> pageUsers(Page<User> page, User user);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> allUsers();

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    List<User> listByDeptId(Long deptId);
    
    /**
     * 获取用户的角色和权限信息
     *
     * @param userId 用户ID
     * @return 包含角色和权限的用户信息
     */
    User getUserWithRolesAndPermissions(Long userId);
    
    /**
     * 根据用户名和租户ID查询用户
     *
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 用户信息
     */
    User getByUsernameAndTenantId(String username, String tenantId);
}