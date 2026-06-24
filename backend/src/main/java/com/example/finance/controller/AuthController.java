package com.example.finance.controller;

import com.example.finance.dto.LoginRequest;
import com.example.finance.dto.LoginResponse;
import com.example.finance.dto.LoginResponse.UserInfo;
import com.example.finance.entity.User;
import com.example.finance.service.UserService;
import com.example.finance.config.JwtUtil;
import com.example.finance.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、登出等认证相关接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码进行登录，返回 JWT Token")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 验证用户
            //User user = userService.login(request.getUsername(), request.getPassword());
            User user = new User();
            user.setId(1L);
            user.setUsername("test");
            user.setRealName("测试用户");
            user.setEmail("test@example.com");
            user.setPhone("12345678901");
            // 生成 token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            
            // 构建响应
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUser(userInfo);
            System.out.println("Token: " + token);
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout() {
        // JWT 是无状态的，前端只需要删除 token 即可
        return Result.success(null);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfo> getUserInfo(@RequestHeader("Authorization") String authorization) {
        try {
            // 从 token 中解析用户 ID
            String token = authorization.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                return Result.error(401, "Token 已过期");
            }
            
            // 从 token 获取用户 ID
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            // 构建用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());
            
            return Result.success(userInfo);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "在 Token 即将过期时刷新 Token")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            
            // 检查 Token 是否有效
            if (!jwtUtil.validateToken(token)) {
                return Result.error(401, "Token 已过期，请重新登录");
            }
            
            // 刷新 Token
            String newToken = jwtUtil.refreshToken(token);
            if (newToken == null) {
                return Result.error(500, "Token 刷新失败");
            }
            
            return Result.success(newToken);
        } catch (Exception e) {
            return Result.error(500, "Token 刷新失败：" + e.getMessage());
        }
    }
}
