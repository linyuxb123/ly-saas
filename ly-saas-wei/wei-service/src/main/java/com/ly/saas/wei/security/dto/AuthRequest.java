package com.ly.saas.wei.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 认证请求 DTO
 */
@Data
public class AuthRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}