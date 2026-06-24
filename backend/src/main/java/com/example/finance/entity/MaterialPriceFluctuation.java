package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 材料采购价格波动实体
 */
@Data
@TableName("material_price_fluctuation")
public class MaterialPriceFluctuation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 材料编码
     */
    private String materialCode;
    
    /**
     * 材料名称
     */
    private String materialName;
    
    /**
     * 供应商 ID
     */
    private Long supplierId;
    
    /**
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 采购日期
     */
    private Date purchaseDate;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    
    /**
     * 数量
     */
    private BigDecimal quantity;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 价格波动率
     */
    private BigDecimal priceChangeRate;
    
    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;
    
    /**
     * 预算差异
     */
    private BigDecimal budgetVariance;
    
    /**
     * 会计期间
     */
    private String accountingPeriod;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
