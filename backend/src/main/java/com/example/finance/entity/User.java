package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    /**
     * 用户 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：ENABLED-正常，DISABLED-禁用
     */
    private String status;

    /**
     * 部门代码
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色
     */
    private String role;

    /**
     * 创建时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("create_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("update_time")
    private LocalDateTime updateTime;
}
