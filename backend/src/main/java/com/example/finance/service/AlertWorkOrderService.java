package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.AlertWorkOrder;

import java.util.List;

/**
 * 预警工单 Service 接口
 */
public interface AlertWorkOrderService extends IService<AlertWorkOrder> {
    
    /**
     * 查询所有工单
     */
    List<AlertWorkOrder> listAll();
    
    /**
     * 分页查询工单
     */
    Page<AlertWorkOrder> pageWorkOrder(Integer pageNum, Integer pageSize);
    
    /**
     * 根据预警 ID 查询工单
     */
    List<AlertWorkOrder> listByAlertId(Long alertId);
    
    /**
     * 根据状态查询工单
     */
    List<AlertWorkOrder> listByStatus(String status);
    
    /**
     * 根据处理人查询工单
     */
    List<AlertWorkOrder> listByAssignee(Long assigneeId);
    
    /**
     * 创建工单
     */
    boolean createWorkOrder(AlertWorkOrder workOrder);
    
    /**
     * 分配工单
     */
    boolean assignWorkOrder(Long id, Long assigneeId, String assigneeName);
    
    /**
     * 解决工单
     */
    boolean resolveWorkOrder(Long id, String solution);
    
    /**
     * 关闭工单
     */
    boolean closeWorkOrder(Long id, Long closedBy);
    
    /**
     * 驳回工单
     */
    boolean rejectWorkOrder(Long id, String reason);
}
