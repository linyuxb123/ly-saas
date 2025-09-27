package com.ly.saas.wu.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.saas.wu.core.constant.Constants;
import com.ly.saas.wu.core.entity.User;
import com.ly.saas.wu.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController(Constants.PREFIX + "UserController")
@RequestMapping(Constants.API_PREFIX + "/user")
public class UserController {

    @Resource(name = "wuUserService")
    private UserService userService;

    /**
     * 获取版本信息
     *
     * @return
     */
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/getVersion")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok(Constants.PREFIX);
    }

    /**
     * 获取用户列表
     *
     * @param user     查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/list")
    public ResponseEntity<Page<User>> list(User user,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = userService.pageUsers(page, user);
        return ResponseEntity.ok(userPage);
    }

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> allUsers() {
        log.info(Constants.PREFIX + "获取所有用户列表");
        List<User> allUsers = userService.allUsers();
        return ResponseEntity.ok(allUsers);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getInfo(@PathVariable("id") Long id) {
        log.info(Constants.PREFIX + "获取用户详情，用户ID：{}", id);
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping
    public ResponseEntity<Boolean> add(@RequestBody User user) {
        boolean result = userService.save(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody User user) {
        boolean result = userService.updateById(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 结果
     */
    @PreAuthorize("hasAuthority('user:write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") Long id) {
        boolean result = userService.removeById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<List<User>> listByDeptId(@PathVariable("deptId") Long deptId) {
        List<User> users = userService.listByDeptId(deptId);
        return ResponseEntity.ok(users);
    }
}