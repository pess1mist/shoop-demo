package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.WorkflowRule;
import com.example.finance.mapper.WorkflowRuleMapper;
import com.example.finance.service.WorkflowRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 审批规则 Service 实现类
 */
@Slf4j
@Service
public class WorkflowRuleServiceImpl extends ServiceImpl<WorkflowRuleMapper, WorkflowRule> implements WorkflowRuleService {
    
    @Override
    public List<WorkflowRule> listActiveRules() {
        LambdaQueryWrapper<WorkflowRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowRule::getStatus, "ACTIVE")
               .orderByDesc(WorkflowRule::getPriority);
        return this.list(wrapper);
    }
    
    @Override
    public List<WorkflowRule> listByProcessKey(String processKey) {
        LambdaQueryWrapper<WorkflowRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowRule::getProcessKey, processKey)
               .eq(WorkflowRule::getStatus, "ACTIVE")
               .orderByDesc(WorkflowRule::getPriority);
        return this.list(wrapper);
    }
    
    @Override
    public List<WorkflowRule> listByProcessKeyAndType(String processKey, String ruleType) {
        LambdaQueryWrapper<WorkflowRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowRule::getProcessKey, processKey)
               .eq(WorkflowRule::getRuleType, ruleType)
               .eq(WorkflowRule::getStatus, "ACTIVE")
               .orderByDesc(WorkflowRule::getPriority);
        return this.list(wrapper);
    }
    
    @Override
    public WorkflowRule matchRule(String processKey, BigDecimal businessData) {
        // 获取该流程的所有启用规则
        List<WorkflowRule> rules = listByProcessKey(processKey);
        
        if (rules == null || rules.isEmpty()) {
            log.info("流程 {} 没有配置审批规则", processKey);
            return null;
        }
        
        // 按优先级排序，找到第一个匹配的规则
        for (WorkflowRule rule : rules) {
            if (testRule(rule, businessData)) {
                log.info("匹配到规则：{}, 优先级：{}", rule.getRuleName(), rule.getPriority());
                return rule;
            }
        }
        
        log.info("流程 {} 没有匹配到适用的规则", processKey);
        return null;
    }
    
    @Override
    public boolean testRule(WorkflowRule rule, BigDecimal businessData) {
        if (rule == null || businessData == null) {
            return false;
        }
        
        // 金额阈值规则
        if ("AMOUNT".equals(rule.getRuleType())) {
            BigDecimal threshold = rule.getThresholdAmount();
            if (threshold == null) {
                return false;
            }
            
            // 业务数据 >= 阈值时匹配
            return businessData.compareTo(threshold) >= 0;
        }
        
        // TODO: 条件分支规则（解析conditionExpr JSON）
        return false;
    }
    
    @Override
    public boolean createRule(WorkflowRule rule) {
        try {
            rule.setStatus("ACTIVE");
            return this.save(rule);
        } catch (Exception e) {
            log.error("创建审批规则失败", e);
            return false;
        }
    }
    
    @Override
    public boolean updateRule(WorkflowRule rule) {
        try {
            return this.updateById(rule);
        } catch (Exception e) {
            log.error("更新审批规则失败", e);
            return false;
        }
    }
    
    @Override
    public boolean enableRule(Long ruleId) {
        try {
            WorkflowRule rule = this.getById(ruleId);
            if (rule == null) {
                log.error("规则不存在，ruleId: {}", ruleId);
                return false;
            }
            rule.setStatus("ACTIVE");
            return this.updateById(rule);
        } catch (Exception e) {
            log.error("启用规则失败", e);
            return false;
        }
    }
    
    @Override
    public boolean disableRule(Long ruleId) {
        try {
            WorkflowRule rule = this.getById(ruleId);
            if (rule == null) {
                log.error("规则不存在，ruleId: {}", ruleId);
                return false;
            }
            rule.setStatus("INACTIVE");
            return this.updateById(rule);
        } catch (Exception e) {
            log.error("停用规则失败", e);
            return false;
        }
    }
    
    @Override
    public boolean deleteRule(Long ruleId) {
        try {
            return this.removeById(ruleId);
        } catch (Exception e) {
            log.error("删除规则失败", e);
            return false;
        }
    }
}
