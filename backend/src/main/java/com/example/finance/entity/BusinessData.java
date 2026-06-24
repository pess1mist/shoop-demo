package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("business_data")
public class BusinessData {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String businessCode;
    private String businessType;
    private String departmentCode;
    private String projectName;
    private BigDecimal amount;
    private LocalDateTime businessDate;
    private String status;
    private String sourceSystem;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
