package com.example.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本结构 VO（用于返回给前端）
 */
@Data
public class CostStructureVO {

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份 (yyyy-MM)
     */
    private String month;

    /**
     * 材料费
     */
    private BigDecimal materialCost;

    /**
     * 人工费
     */
    private BigDecimal laborCost;

    /**
     * 制造费用
     */
    private BigDecimal overheadCost;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

    /**
     * 材料费占比
     */
    private BigDecimal materialPercent;

    /**
     * 人工费占比
     */
    private BigDecimal laborPercent;

    /**
     * 制造费用占比
     */
    private BigDecimal overheadPercent;

    /**
     * 单位成本（元/吨）
     */
    private BigDecimal unitCost;

    /**
     * 总产量（吨）
     */
    private Integer totalQuantity;
}
