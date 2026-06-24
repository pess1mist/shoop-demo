package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("trend_prediction")
public class TrendPrediction {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String predictionType;
    private String departmentCode;
    private String projectName;
    private BigDecimal predictedValue;
    private BigDecimal actualValue;
    private LocalDateTime predictionTime;
    private LocalDateTime targetTime;
    private String confidenceLevel;
    private String algorithm;
    private String analysisResult;
    private String suggestion;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
