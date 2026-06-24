package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.WorkflowRule;

import java.math.BigDecimal;
import java.util.List;

/**
 * 审批规则 Service 接口
 */
public interface WorkflowRuleService extends IService<WorkflowRule> {
    
    /**
     * 查询所有启用的规则
     */
    List<WorkflowRule> listActiveRules();
    
    /**
     * 根据流程标识查询规则
     */
    List<WorkflowRule> listByProcessKey(String processKey);
    
    /**
     * 根据流程标识和规则类型查询
     */
    List<WorkflowRule> listByProcessKeyAndType(String processKey, String ruleType);
    
    /**
     * 匹配审批规则
     * 根据业务数据匹配适用的规则，返回优先级最高的规则
     * 
     * @param processKey 流程标识
     * @param businessData 业务数据（如金额等）
     * @return 匹配的规则，如果没有匹配则返回null
     */
    WorkflowRule matchRule(String processKey, BigDecimal businessData);
    
    /**
     * 测试规则是否匹配
     * 
     * @param rule 规则
     * @param businessData 业务数据
     * @return 是否匹配
     */
    boolean testRule(WorkflowRule rule, BigDecimal businessData);
    
    /**
     * 创建规则
     */
    boolean createRule(WorkflowRule rule);
    
    /**
     * 更新规则
     */
    boolean updateRule(WorkflowRule rule);
    
    /**
     * 启用规则
     */
    boolean enableRule(Long ruleId);
    
    /**
     * 停用规则
     */
    boolean disableRule(Long ruleId);
    
    /**
     * 删除规则
     */
    boolean deleteRule(Long ruleId);
}
