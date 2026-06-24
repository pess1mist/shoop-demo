package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预警工单实体类
 */
@Data
@TableName("alert_work_order")
public class AlertWorkOrder {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联内控日志 ID
     */
    private Long alertId;
    
    /**
     * 预警类型（BUDGET_OVER-预算超支，PRICE_ABNORMAL-价格异常，COST_ABNORMAL-成本异常）
     */
    private String alertType;
    
    /**
     * 工单标题
     */
    private String title;
    
    /**
     * 问题描述
     */
    private String description;
    
    /**
     * 优先级（HIGH-高，MEDIUM-中，LOW-低）
     */
    private String priority;
    
    /**
     * 处理人 ID
     */
    private Long assigneeId;
    
    /**
     * 处理人姓名
     */
    private String assigneeName;
    
    /**
     * 状态（NEW-新建，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭，REJECTED-已驳回）
     */
    private String status;
    
    /**
     * 解决方案
     */
    private String solution;
    
    /**
     * 解决时间
     */
    private LocalDateTime resolvedTime;
    
    /**
     * 关闭人 ID
     */
    private Long closedBy;
    
    /**
     * 关闭时间
     */
    private LocalDateTime closedTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 备注
     */
    private String remark;
}
