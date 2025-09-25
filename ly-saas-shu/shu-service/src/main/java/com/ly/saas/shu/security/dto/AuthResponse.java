package com.ly.saas.shu.security.dto;

import com.ly.saas.shu.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token 类型
     */
    private String tokenType = "Bearer";

    /**
     * 用户信息
     */
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}