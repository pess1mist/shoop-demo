package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("monitor_alert")
public class MonitorAlert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String alertCode;
    private String alertType;
    private String alertLevel;
    private String alertTitle;
    private String alertContent;
    private BigDecimal thresholdValue;
    private BigDecimal actualValue;
    private LocalDateTime alertTime;
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
