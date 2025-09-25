package com.ly.saas.shu.api.controller;

import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.core.entity.User;
import com.ly.saas.shu.security.JwtTokenUtil;
import com.ly.saas.shu.security.SaaSUserDetails;
import com.ly.saas.shu.security.dto.AuthRequest;
import com.ly.saas.shu.security.dto.AuthResponse;
import com.ly.saas.shu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RestController
@RequestMapping(Constants.API_PREFIX + "/auth")
public class AuthController {

    @Autowired
    @Qualifier(Constants.PREFIX + "AuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier(Constants.PREFIX + "JwtTokenUtil")
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier(Constants.PREFIX + "UserService")
    private UserService userService;

    @Autowired
    @Qualifier(Constants.PREFIX + "PasswordEncoder")
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
        String token = jwtTokenUtil.generateToken(saasUserDetails, authRequest.getTenantId());
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