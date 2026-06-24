package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.PurchasePlan;

import java.util.List;

/**
 * 采购计划 Service 接口
 */
public interface PurchasePlanService extends IService<PurchasePlan> {
    
    /**
     * 查询所有采购计划
     */
    List<PurchasePlan> listAll();
    
    /**
     * 分页查询采购计划
     */
    Page<PurchasePlan> pagePurchasePlan(Integer pageNum, Integer pageSize);
    
    /**
     * 根据部门查询采购计划
     */
    List<PurchasePlan> listByDept(String deptCode);
    
    /**
     * 根据预算 ID 查询采购计划
     */
    List<PurchasePlan> listByBudgetId(Long budgetId);
    
    /**
     * 根据状态查询采购计划
     */
    List<PurchasePlan> listByStatus(String status);
    
    /**
     * 创建采购计划
     */
    boolean createPurchasePlan(PurchasePlan purchasePlan);
    
    /**
     * 提交审批
     */
    boolean submitForApproval(Long planId, Long userId, String userName);
    
    /**
     * 审批通过
     */
    boolean approvePlan(Long planId, Long approverId, String approverName, String opinion);
    
    /**
     * 驳回
     */
    boolean rejectPlan(Long planId, Long approverId, String approverName, String opinion);
    
    /**
     * 完成采购
     */
    boolean completePlan(Long planId);
    
    /**
     * 查询采购计划的审批进度
     * @param planId 采购计划 ID
     * @return 审批进度信息
     */
    Object getWorkflowProgress(Long planId);
}
