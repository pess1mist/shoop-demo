package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算主表实体类
 */
@Data
@TableName("budget_master")
public class BudgetMaster {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 预算年度
     */
    private Integer year;
    
    /**
     * 预算期间（季度或月份）
     */
    private String period;
    
    /**
     * 部门代码
     */
    private String deptCode;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 项目代码
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 预算类型（费用/成本/收入）
     */
    private String budgetType;
    
    /**
     * 预算科目
     */
    private String budgetItem;
    
    /**
     * 预算金额（元）
     */
    private BigDecimal amount;
    
    /**
     * 状态（DRAFT-草稿，APPROVED-已审批，REJECTED-已驳回）
     */
    private String status;
    
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
     * 审批人 ID
     */
    private Long approvedBy;
    
    /**
     * 审批人姓名
     */
    private String approvedByName;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvedTime;
    
    /**
     * 备注
     */
    private String remark;
}
