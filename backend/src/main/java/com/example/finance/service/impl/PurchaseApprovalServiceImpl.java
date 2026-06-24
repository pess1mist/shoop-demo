package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.PurchaseApproval;
import com.example.finance.entity.PurchasePlan;
import com.example.finance.mapper.PurchaseApprovalMapper;
import com.example.finance.service.PurchaseApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购审批 Service 实现类
 */
@Slf4j
@Service
public class PurchaseApprovalServiceImpl extends ServiceImpl<PurchaseApprovalMapper, PurchaseApproval> implements PurchaseApprovalService {
    
    @Override
    public List<PurchaseApproval> listAll() {
        return this.list();
    }
    
    @Override
    public Page<PurchaseApproval> pageApproval(Integer pageNum, Integer pageSize) {
        Page<PurchaseApproval> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<PurchaseApproval> listByPlanId(Long planId) {
        LambdaQueryWrapper<PurchaseApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseApproval::getPlanId, planId);
        return this.list(wrapper);
    }
    
    @Override
    public List<PurchaseApproval> listByApprover(Long approverId) {
        LambdaQueryWrapper<PurchaseApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseApproval::getApproverId, approverId);
        return this.list(wrapper);
    }
    
    @Override
    public List<PurchaseApproval> listByResult(String result) {
        LambdaQueryWrapper<PurchaseApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseApproval::getApprovalResult, result);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createApproval(PurchaseApproval approval) {
        try {
            approval.setApprovalResult("PENDING");
            return this.save(approval);
        } catch (Exception e) {
            log.error("创建审批记录失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doApproval(Long id, String result, String opinion) {
        try {
            PurchaseApproval approval = this.getById(id);
            if (approval == null) {
                log.error("审批记录不存在，id: {}", id);
                return false;
            }
            
            approval.setApprovalResult(result);
            approval.setApprovalOpinion(opinion);
            approval.setApprovalTime(LocalDateTime.now());
            
            return this.updateById(approval);
        } catch (Exception e) {
            log.error("审批失败", e);
            return false;
        }
    }
}
