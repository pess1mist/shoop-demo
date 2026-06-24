package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 供应商实体类
 */
@Data
@TableName("supplier")
public class Supplier {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 供应商代码
     */
    private String supplierCode;
    
    /**
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 联系人
     */
    private String contact;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 评级
     */
    private String rating;
}
