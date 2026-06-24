package com.example.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本趋势 VO（用于返回给前端）
 */
@Data
public class CostTrendVO {

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份（格式：2025-01）
     */
    private String month;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 单位成本（元/吨）
     */
    private BigDecimal unitCost;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;
}
