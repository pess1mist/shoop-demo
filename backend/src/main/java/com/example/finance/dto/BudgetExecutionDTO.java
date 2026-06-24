package com.example.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 预算执行对比 DTO
 */
@Data
public class BudgetExecutionDTO {
    private String period;  // 期间 (yyyy-MM)
    private java.math.BigDecimal budget;
    private java.math.BigDecimal actual;
    private java.math.BigDecimal variance;
    private java.math.BigDecimal varianceRate;
}
