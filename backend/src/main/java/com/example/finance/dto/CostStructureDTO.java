package com.example.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 完全成本结构 DTO
 */
@Data
public class CostStructureDTO {
    private String month;
    private BigDecimal materialRate;
    private BigDecimal laborRate;
    private BigDecimal manuRate;
}
