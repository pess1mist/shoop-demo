package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("financial_data")
public class FinancialData {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String voucherCode;
    private String subjectCode;
    private String subjectName;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private BigDecimal balance;
    private LocalDateTime voucherDate;
    private String businessCode;
    private Long businessDataId;
    private String accountingPeriod;
    private String costCenter;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
