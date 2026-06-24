package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 生产订单实体类
 */
@Data
@TableName("production_order")
public class ProductionOrder {

    /**
     * 生产订单 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String prodOrderId;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 计划产量（吨）
     */
    private Integer planQuantity;

    /**
     * 实际产量（吨）
     */
    private Integer actualQuantity;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 材料成本（元）
     */
    private Integer materialCost;

    /**
     * 人工成本（元）
     */
    private Integer laborCost;

    /**
     * 制造费用（元）
     */
    private Integer manuCost;

    /**
     * 总成本（元）
     */
    private Integer totalCost;

    /**
     * 订单状态：生产中/已完成/暂停/异常
     */
    private String status;
}
