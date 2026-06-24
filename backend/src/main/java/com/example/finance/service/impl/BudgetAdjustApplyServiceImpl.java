package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.BudgetAdjustApply;
import com.example.finance.entity.BudgetMaster;
import com.example.finance.mapper.BudgetAdjustApplyMapper;
import com.example.finance.service.BudgetAdjustApplyService;
import com.example.finance.service.BudgetMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算调整申请 Service 实现类
 */
@Slf4j
@Service
public class BudgetAdjustApplyServiceImpl extends ServiceImpl<BudgetAdjustApplyMapper, BudgetAdjustApply> implements BudgetAdjustApplyService {
    
    @Autowired
    private BudgetMasterService budgetMasterService;
    
    @Override
    public List<BudgetAdjustApply> listAll() {
        return this.list();
    }
    
    @Override
    public Page<BudgetAdjustApply> pageAdjustApply(Integer pageNum, Integer pageSize) {
        Page<BudgetAdjustApply> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<BudgetAdjustApply> listByBudgetId(Long budgetId) {
        LambdaQueryWrapper<BudgetAdjustApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetAdjustApply::getBudgetId, budgetId);
        return this.list(wrapper);
    }
    
    @Override
    public List<BudgetAdjustApply> listByStatus(String status) {
        LambdaQueryWrapper<BudgetAdjustApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetAdjustApply::getStatus, status);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveApply(Long id, Long approverId, String approverName, String remark) {
        try {
            BudgetAdjustApply apply = this.getById(id);
            if (apply == null) {
                log.error("调整申请不存在，id: {}", id);
                return false;
            }
            
            // 更新申请状态
            apply.setStatus("APPROVED");
            apply.setApproverId(approverId);
            apply.setApproverName(approverName);
            apply.setApproveTime(LocalDateTime.now());
            apply.setApproveRemark(remark);
            
            if (!this.updateById(apply)) {
                return false;
            }
            
            // 更新主预算表的金额
            BudgetMaster budget = budgetMasterService.getById(apply.getBudgetId());
            if (budget != null) {
                if ("增加".equals(apply.getAdjustType())) {
                    budget.setAmount(apply.getAfterAmount());
                } else if ("减少".equals(apply.getAdjustType())) {
                    budget.setAmount(apply.getAfterAmount());
                }
                budgetMasterService.updateById(budget);
            }
            
            return true;
        } catch (Exception e) {
            log.error("审批调整申请失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectApply(Long id, Long approverId, String approverName, String remark) {
        try {
            BudgetAdjustApply apply = this.getById(id);
            if (apply == null) {
                log.error("调整申请不存在，id: {}", id);
                return false;
            }
            
            apply.setStatus("REJECTED");
            apply.setApproverId(approverId);
            apply.setApproverName(approverName);
            apply.setApproveTime(LocalDateTime.now());
            apply.setApproveRemark(remark);
            
            return this.updateById(apply);
        } catch (Exception e) {
            log.error("驳回调整申请失败", e);
            return false;
        }
    }
}
