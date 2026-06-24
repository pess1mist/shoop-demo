package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 内控预警日志实体类
 */
@Data
@TableName("internal_control_log")
public class InternalControlLog {

    /**
     * 日志 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String logId;

    /**
     * 预警类型（超预算/价格异常/成本异常等）
     */
    private String alertType;

    /**
     * 相关单据号
     */
    private String relatedDocNo;

    /**
     * 预警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime alertTime;

    /**
     * 预警内容
     */
    private String alertContent;

    /**
     * 处理状态（已处理/处理中/未处理）
     */
    private String handleStatus;

    /**
     * 处理人
     */
    private String handler;
}
