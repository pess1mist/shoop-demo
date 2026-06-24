package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 产品实体类
 */
@Data
@TableName("product")
public class Product {
    
    /**
     * 产品代码
     */
    @TableId(value = "product_code")
    private String productCode;
    
    /**
     * 产品名称
     */
    @TableField("product_name")
    private String productName;
    
    /**
     * 单位
     */
    @TableField("unit")
    private String unit;
    
    /**
     * 单价（元/吨）
     */
    @TableField("unit_price")
    private java.math.BigDecimal unitPrice;
    
    /**
     * 成本价（元/吨）
     */
    @TableField("cost_price")
    private java.math.BigDecimal costPrice;
}
