package com.example.finance.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 内控预警 DTO
 */
@Data
public class AlertDTO {
    private String alertId;
    private String alertType;
    private String alertLevel;
    private String alertContent;
    private BigDecimal amount;
    private LocalDateTime alertTime;
    private String status;
    private String processor;
}
