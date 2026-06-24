package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.BudgetMaster;
import com.example.finance.mapper.BudgetMasterMapper;
import com.example.finance.service.BudgetMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算主表 Service 实现类
 */
@Slf4j
@Service
public class BudgetMasterServiceImpl extends ServiceImpl<BudgetMasterMapper, BudgetMaster> implements BudgetMasterService {
    
    @Override
    public List<BudgetMaster> listAll() {
        return this.list();
    }
    
    @Override
    public Page<BudgetMaster> pageBudget(Integer pageNum, Integer pageSize) {
        Page<BudgetMaster> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<BudgetMaster> listByYear(Integer year) {
        LambdaQueryWrapper<BudgetMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetMaster::getYear, year);
        return this.list(wrapper);
    }
    
    @Override
    public List<BudgetMaster> listByDept(String deptCode) {
        LambdaQueryWrapper<BudgetMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetMaster::getDeptCode, deptCode);
        return this.list(wrapper);
    }
    
    @Override
    public List<BudgetMaster> listByStatus(String status) {
        LambdaQueryWrapper<BudgetMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetMaster::getStatus, status);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveBudget(Long id, Long approverId, String approverName) {
        try {
            BudgetMaster budget = this.getById(id);
            if (budget == null) {
                log.error("预算不存在，id: {}", id);
                return false;
            }
            
            budget.setStatus("APPROVED");
            budget.setApprovedBy(approverId);
            budget.setApprovedByName(approverName);
            budget.setApprovedTime(LocalDateTime.now());
            
            return this.updateById(budget);
        } catch (Exception e) {
            log.error("审批预算失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectBudget(Long id, Long approverId, String approverName, String reason) {
        try {
            BudgetMaster budget = this.getById(id);
            if (budget == null) {
                log.error("预算不存在，id: {}", id);
                return false;
            }
            
            budget.setStatus("REJECTED");
            budget.setApprovedBy(approverId);
            budget.setApprovedByName(approverName);
            budget.setApprovedTime(LocalDateTime.now());
            budget.setRemark(reason);
            
            return this.updateById(budget);
        } catch (Exception e) {
            log.error("驳回预算失败", e);
            return false;
        }
    }
}
