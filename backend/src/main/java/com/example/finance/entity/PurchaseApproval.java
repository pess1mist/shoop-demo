package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 采购审批表实体类
 */
@Data
@TableName("purchase_approval")
public class PurchaseApproval {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联采购计划 ID
     */
    private Long planId;
    
    /**
     * 审批类型（预算内/预算外）
     */
    private String approvalType;
    
    /**
     * 审批层级
     */
    private Integer approvalLevel;
    
    /**
     * 审批人 ID
     */
    private Long approverId;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批结果（PENDING-待审批，APPROVED-已批准，REJECTED-已驳回）
     */
    private String approvalResult;
    
    /**
     * 审批意见
     */
    private String approvalOpinion;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 备注
     */
    private String remark;
}
