package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("budget_control")
public class BudgetControl {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String budgetCode;
    private String budgetType;
    private String departmentCode;
    private String projectName;
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;
    private BigDecimal controlRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
