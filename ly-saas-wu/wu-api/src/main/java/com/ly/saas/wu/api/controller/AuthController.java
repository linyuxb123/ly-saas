package com.ly.saas.wu.api.controller;

import com.ly.saas.common.config.TenantHolder;
import com.ly.saas.wu.core.constant.Constants;
import com.ly.saas.wu.core.entity.User;
import com.ly.saas.wu.security.JwtTokenUtil;
import com.ly.saas.wu.security.SaaSUserDetails;
import com.ly.saas.wu.security.dto.AuthRequest;
import com.ly.saas.wu.security.dto.AuthResponse;
import com.ly.saas.wu.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController(Constants.PREFIX + "AuthController")
@RequestMapping(Constants.API_PREFIX + "/auth")
public class AuthController {

    @Lazy
    @Resource
    private AuthenticationManager authenticationManager;

    @Lazy
    @Autowired
    @Qualifier(Constants.PREFIX + "JwtTokenUtil")
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier(Constants.PREFIX + "UserService")
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     *
     * @param authRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        // 验证用户名和密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 获取用户详情
        SaaSUserDetails saasUserDetails = (SaaSUserDetails) authentication.getPrincipal();
        User user = saasUserDetails.getUser();
        // 生成JWT Token
        String token = jwtTokenUtil.generateToken(saasUserDetails, TenantHolder.getTenantCode());
        // 返回认证结果
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.getByUsernameAndTenantId(user.getUsername(), user.getTenantId());
        if (existingUser != null) {
            return ResponseEntity.badRequest().build();
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认状态
        user.setStatus(1);
        // 保存用户
        userService.save(user);
        // 清除密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}