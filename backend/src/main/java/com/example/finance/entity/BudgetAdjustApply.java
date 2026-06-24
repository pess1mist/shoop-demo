package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算调整申请表实体类
 */
@Data
@TableName("budget_adjust_apply")
public class BudgetAdjustApply {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联预算主表 ID
     */
    private Long budgetId;
    
    /**
     * 调整类型（增加/减少）
     */
    private String adjustType;
    
    /**
     * 调整金额
     */
    private BigDecimal adjustAmount;
    
    /**
     * 调整原因
     */
    private String adjustReason;
    
    /**
     * 调整前金额
     */
    private BigDecimal beforeAmount;
    
    /**
     * 调整后金额
     */
    private BigDecimal afterAmount;
    
    /**
     * 状态（PENDING-待审批，APPROVED-已批准，REJECTED-已驳回）
     */
    private String status;
    
    /**
     * 申请人 ID
     */
    private Long applicantId;
    
    /**
     * 申请人姓名
     */
    private String applicantName;
    
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    
    /**
     * 审批人 ID
     */
    private Long approverId;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批时间
     */
    private LocalDateTime approveTime;
    
    /**
     * 审批意见
     */
    private String approveRemark;
}
