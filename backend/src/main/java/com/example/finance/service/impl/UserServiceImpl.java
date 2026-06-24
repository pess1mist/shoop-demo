package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.finance.entity.User;
import com.example.finance.mapper.UserMapper;
import com.example.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User login(String username, String password) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 查询用户
        User user = findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 校验密码（MD5 加密）
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 检查用户状态，兼容历史数据（1/0）和字符串状态（ENABLED/DISABLED）
        String status = user.getStatus();
        boolean enabled = "ENABLED".equalsIgnoreCase(status)
                || "1".equals(status)
                || "true".equalsIgnoreCase(status)
                || "\u0001".equals(status);
        if (!enabled) {
            throw new IllegalArgumentException("用户已被禁用");
        }

        return user;
    }
}
