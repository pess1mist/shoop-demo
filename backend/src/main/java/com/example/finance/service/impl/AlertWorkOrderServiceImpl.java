package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.AlertWorkOrder;
import com.example.finance.mapper.AlertWorkOrderMapper;
import com.example.finance.service.AlertWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警工单 Service 实现类
 */
@Slf4j
@Service
public class AlertWorkOrderServiceImpl extends ServiceImpl<AlertWorkOrderMapper, AlertWorkOrder> implements AlertWorkOrderService {
    
    @Override
    public List<AlertWorkOrder> listAll() {
        return this.list();
    }
    
    @Override
    public Page<AlertWorkOrder> pageWorkOrder(Integer pageNum, Integer pageSize) {
        Page<AlertWorkOrder> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<AlertWorkOrder> listByAlertId(Long alertId) {
        LambdaQueryWrapper<AlertWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertWorkOrder::getAlertId, alertId);
        return this.list(wrapper);
    }
    
    @Override
    public List<AlertWorkOrder> listByStatus(String status) {
        LambdaQueryWrapper<AlertWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertWorkOrder::getStatus, status);
        return this.list(wrapper);
    }
    
    @Override
    public List<AlertWorkOrder> listByAssignee(Long assigneeId) {
        LambdaQueryWrapper<AlertWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertWorkOrder::getAssigneeId, assigneeId);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createWorkOrder(AlertWorkOrder workOrder) {
        try {
            workOrder.setStatus("NEW");
            workOrder.setCreatedTime(LocalDateTime.now());
            return this.save(workOrder);
        } catch (Exception e) {
            log.error("创建工单失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignWorkOrder(Long id, Long assigneeId, String assigneeName) {
        try {
            AlertWorkOrder workOrder = this.getById(id);
            if (workOrder == null) {
                log.error("工单不存在，id: {}", id);
                return false;
            }
            
            workOrder.setAssigneeId(assigneeId);
            workOrder.setAssigneeName(assigneeName);
            workOrder.setStatus("PROCESSING");
            
            return this.updateById(workOrder);
        } catch (Exception e) {
            log.error("分配工单失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resolveWorkOrder(Long id, String solution) {
        try {
            AlertWorkOrder workOrder = this.getById(id);
            if (workOrder == null) {
                log.error("工单不存在，id: {}", id);
                return false;
            }
            
            workOrder.setSolution(solution);
            workOrder.setStatus("RESOLVED");
            workOrder.setResolvedTime(LocalDateTime.now());
            
            return this.updateById(workOrder);
        } catch (Exception e) {
            log.error("解决工单失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeWorkOrder(Long id, Long closedBy) {
        try {
            AlertWorkOrder workOrder = this.getById(id);
            if (workOrder == null) {
                log.error("工单不存在，id: {}", id);
                return false;
            }
            
            workOrder.setClosedBy(closedBy);
            workOrder.setClosedTime(LocalDateTime.now());
            workOrder.setStatus("CLOSED");
            
            return this.updateById(workOrder);
        } catch (Exception e) {
            log.error("关闭工单失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectWorkOrder(Long id, String reason) {
        try {
            AlertWorkOrder workOrder = this.getById(id);
            if (workOrder == null) {
                log.error("工单不存在，id: {}", id);
                return false;
            }
            
            workOrder.setStatus("REJECTED");
            workOrder.setRemark(reason);
            
            return this.updateById(workOrder);
        } catch (Exception e) {
            log.error("驳回工单失败", e);
            return false;
        }
    }
}
