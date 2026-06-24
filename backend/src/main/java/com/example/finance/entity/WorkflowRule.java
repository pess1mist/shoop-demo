package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 审批规则实体类
 * 用于配置审批流程的触发条件和路由规则
 */
@Data
@TableName("workflow_rule")
public class WorkflowRule {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型：AMOUNT-金额阈值, CONDITION-条件分支
     */
    private String ruleType;
    
    /**
     * 流程标识（触发此规则的流程）
     */
    private String processKey;
    
    /**
     * 条件表达式JSON格式
     * 例如：{"field": "totalAmount", "operator": ">=", "value": 500000}
     */
    private String conditionExpr;
    
    /**
     * 目标流程标识（条件分支时使用）
     */
    private String targetProcessKey;
    
    /**
     * 目标节点标识（跳转到指定节点）
     */
    private String targetNodeKey;
    
    /**
     * 金额阈值
     */
    private BigDecimal thresholdAmount;
    
    /**
     * 优先级，数字越大优先级越高
     */
    private Integer priority;
    
    /**
     * 状态：ACTIVE-启用, INACTIVE-停用
     */
    private String status;
    
    /**
     * 规则描述
     */
    private String description;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 创建人姓名
     */
    private String createdByName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
