package com.example.finance.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
