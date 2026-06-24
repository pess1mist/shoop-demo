package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 采购价格历史实体类
 */
@Data
@TableName("purchase_price_history")
public class PurchasePriceHistory {

    /**
     * 自增 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 材料编码
     */
    private String materialCode;

    /**
     * 日期
     */
    private LocalDate orderDate;

    /**
     * 供应商 ID
     */
    private String supplierId;

    /**
     * 单价（元/吨）
     */
    private BigDecimal unitPrice;

    /**
     * 备注（价格说明）
     */
    private String remark;
}
