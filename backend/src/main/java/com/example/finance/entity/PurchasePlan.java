package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购计划单实体类
 */
@Data
@TableName("purchase_plan")
public class PurchasePlan {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 计划单号
     */
    private String planCode;
    
    /**
     * 计划名称
     */
    private String planName;
    
    /**
     * 部门代码
     */
    private String deptCode;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 供应商代码
     */
    private String supplierCode;
    
    /**
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 物料代码
     */
    private String materialCode;
    
    /**
     * 物料名称
     */
    private String materialName;
    
    /**
     * 采购数量
     */
    private Integer quantity;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 关联预算 ID
     */
    private Long budgetId;
    
    /**
     * 预算科目
     */
    private String budgetItem;
    
    /**
     * 计划日期
     */
    private LocalDate planDate;
    
    /**
     * 需求日期
     */
    private LocalDate requireDate;
    
    /**
     * 状态（DRAFT-草稿，PENDING-待审批，APPROVED-已批准，REJECTED-已驳回，PURCHASING-采购中，COMPLETED-已完成）
     */
    private String status;
    
    /**
     * 工作流实例 ID
     */
    private Long workflowInstanceId;
    
    /**
     * 创建人 ID
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
     * 备注
     */
    private String remark;
}
