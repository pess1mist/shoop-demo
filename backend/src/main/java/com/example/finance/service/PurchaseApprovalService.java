package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.PurchaseApproval;

import java.util.List;

/**
 * 采购审批 Service 接口
 */
public interface PurchaseApprovalService extends IService<PurchaseApproval> {
    
    /**
     * 查询所有审批记录
     */
    List<PurchaseApproval> listAll();
    
    /**
     * 分页查询审批记录
     */
    Page<PurchaseApproval> pageApproval(Integer pageNum, Integer pageSize);
    
    /**
     * 根据采购计划 ID 查询审批记录
     */
    List<PurchaseApproval> listByPlanId(Long planId);
    
    /**
     * 根据审批人查询审批记录
     */
    List<PurchaseApproval> listByApprover(Long approverId);
    
    /**
     * 根据审批结果查询
     */
    List<PurchaseApproval> listByResult(String result);
    
    /**
     * 创建审批记录
     */
    boolean createApproval(PurchaseApproval approval);
    
    /**
     * 审批
     */
    boolean doApproval(Long id, String result, String opinion);
}
