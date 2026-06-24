package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.BudgetMaster;

import java.util.List;

/**
 * 预算主表 Service 接口
 */
public interface BudgetMasterService extends IService<BudgetMaster> {
    
    /**
     * 查询所有预算
     */
    List<BudgetMaster> listAll();
    
    /**
     * 分页查询预算
     */
    Page<BudgetMaster> pageBudget(Integer pageNum, Integer pageSize);
    
    /**
     * 根据年度查询预算
     */
    List<BudgetMaster> listByYear(Integer year);
    
    /**
     * 根据部门查询预算
     */
    List<BudgetMaster> listByDept(String deptCode);
    
    /**
     * 根据状态查询预算
     */
    List<BudgetMaster> listByStatus(String status);
    
    /**
     * 审批预算
     */
    boolean approveBudget(Long id, Long approverId, String approverName);
    
    /**
     * 驳回预算
     */
    boolean rejectBudget(Long id, Long approverId, String approverName, String reason);
}
