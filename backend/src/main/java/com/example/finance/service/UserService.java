package com.example.finance.service;

import com.example.finance.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
}
