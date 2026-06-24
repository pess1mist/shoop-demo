package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 预算执行实体类
 */
@Data
@TableName("budget_execution")
public class BudgetExecution {

    /**
     * 自增 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 财年
     */
    @NotNull(message = "财年不能为空")
    private Integer fiscalYear;

    /**
     * 期间（yyyy-MM）
     */
    @NotBlank(message = "期间不能为空")
    private String period;

    /**
     * 预算项目（材料费/人工费/制造费用）
     */
    @NotBlank(message = "预算项目不能为空")
    private String budgetItem;

    /**
     * 预算金额（元）
     */
    @NotNull(message = "预算金额不能为空")
    private BigDecimal budgetAmount;

    /**
     * 实际金额（元）
     */
    @NotNull(message = "实际金额不能为空")
    private BigDecimal actualAmount;

    /**
     * 差异（元）
     */
    private BigDecimal variance;

    /**
     * 差异率（%）
     */
    private BigDecimal varianceRate;
}
