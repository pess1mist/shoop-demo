package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.BudgetAdjustApply;

import java.util.List;

/**
 * 预算调整申请 Service 接口
 */
public interface BudgetAdjustApplyService extends IService<BudgetAdjustApply> {
    
    /**
     * 查询所有调整申请
     */
    List<BudgetAdjustApply> listAll();
    
    /**
     * 分页查询调整申请
     */
    Page<BudgetAdjustApply> pageAdjustApply(Integer pageNum, Integer pageSize);
    
    /**
     * 根据预算 ID 查询调整申请
     */
    List<BudgetAdjustApply> listByBudgetId(Long budgetId);
    
    /**
     * 根据状态查询调整申请
     */
    List<BudgetAdjustApply> listByStatus(String status);
    
    /**
     * 审批调整申请
     */
    boolean approveApply(Long id, Long approverId, String approverName, String remark);
    
    /**
     * 驳回调整申请
     */
    boolean rejectApply(Long id, Long approverId, String approverName, String remark);
}
