package com.example.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 材料价格趋势 DTO
 */
@Data
public class PriceTrendDTO {
    private String date;
    private BigDecimal price;
}
