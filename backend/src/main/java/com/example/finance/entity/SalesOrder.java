package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单实体类
 */
@Data
@TableName("sales_order")
public class SalesOrder {

    /**
     * 订单 ID
     */
    @NotBlank(message = "订单 ID 不能为空")
    @TableId(type = IdType.ASSIGN_ID)
    private String orderId;

    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;

    /**
     * 客户 ID
     */
    @NotBlank(message = "客户 ID 不能为空")
    private String customerId;

    /**
     * 产品编码
     */
    @NotBlank(message = "产品编码不能为空")
    private String productCode;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 数量（吨）
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0", message = "数量必须大于 0")
    private BigDecimal quantity;

    /**
     * 单价（元/吨）
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0", message = "单价必须大于 0")
    private BigDecimal unitPrice;

    /**
     * 总金额（元）
     */
    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0", message = "总金额必须大于 0")
    private BigDecimal totalAmount;

    /**
     * 审批状态（已批/待审）
     */
    @NotBlank(message = "审批状态不能为空")
    private String approvalStatus;
}
