package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.WorkflowRule;
import com.example.finance.service.WorkflowRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 审批规则 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow/rule")
@CrossOrigin(origins = "*")
public class WorkflowRuleController {
    
    @Autowired
    private WorkflowRuleService workflowRuleService;
    
    /**
     * 查询所有启用的规则
     */
    @GetMapping("/list")
    public Result<List<WorkflowRule>> listActiveRules() {
        try {
            List<WorkflowRule> rules = workflowRuleService.listActiveRules();
            return Result.success(rules);
        } catch (Exception e) {
            log.error("查询审批规则失败", e);
            return Result.error("查询审批规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据流程标识查询规则
     */
    @GetMapping("/list/{processKey}")
    public Result<List<WorkflowRule>> listByProcessKey(@PathVariable String processKey) {
        try {
            List<WorkflowRule> rules = workflowRuleService.listByProcessKey(processKey);
            return Result.success(rules);
        } catch (Exception e) {
            log.error("查询审批规则失败", e);
            return Result.error("查询审批规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据流程标识和规则类型查询
     */
    @GetMapping("/list/{processKey}/{ruleType}")
    public Result<List<WorkflowRule>> listByProcessKeyAndType(
            @PathVariable String processKey,
            @PathVariable String ruleType) {
        try {
            List<WorkflowRule> rules = workflowRuleService.listByProcessKeyAndType(processKey, ruleType);
            return Result.success(rules);
        } catch (Exception e) {
            log.error("查询审批规则失败", e);
            return Result.error("查询审批规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 匹配审批规则
     */
    @PostMapping("/match")
    public Result<WorkflowRule> matchRule(
            @RequestParam String processKey,
            @RequestParam BigDecimal businessData) {
        try {
            WorkflowRule matchedRule = workflowRuleService.matchRule(processKey, businessData);
            if (matchedRule != null) {
                return Result.success(matchedRule);
            } else {
                return Result.error("没有匹配到适用的规则");
            }
        } catch (Exception e) {
            log.error("匹配审批规则失败", e);
            return Result.error("匹配审批规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 测试规则是否匹配
     */
    @PostMapping("/test")
    public Result<Boolean> testRule(
            @RequestParam Long ruleId,
            @RequestParam BigDecimal businessData) {
        try {
            WorkflowRule rule = workflowRuleService.getById(ruleId);
            if (rule == null) {
                return Result.error("规则不存在");
            }
            boolean matched = workflowRuleService.testRule(rule, businessData);
            return Result.success(matched);
        } catch (Exception e) {
            log.error("测试规则失败", e);
            return Result.error("测试规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建规则
     */
    @PostMapping("/create")
    public Result<Boolean> createRule(@RequestBody WorkflowRule rule) {
        try {
            boolean success = workflowRuleService.createRule(rule);
            return Result.success(success);
        } catch (Exception e) {
            log.error("创建规则失败", e);
            return Result.error("创建规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新规则
     */
    @PutMapping("/update/{ruleId}")
    public Result<Boolean> updateRule(
            @PathVariable Long ruleId,
            @RequestBody WorkflowRule rule) {
        try {
            rule.setId(ruleId);
            boolean success = workflowRuleService.updateRule(rule);
            return Result.success(success);
        } catch (Exception e) {
            log.error("更新规则失败", e);
            return Result.error("更新规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 启用规则
     */
    @PutMapping("/enable/{ruleId}")
    public Result<Boolean> enableRule(@PathVariable Long ruleId) {
        try {
            boolean success = workflowRuleService.enableRule(ruleId);
            return Result.success(success);
        } catch (Exception e) {
            log.error("启用规则失败", e);
            return Result.error("启用规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 停用规则
     */
    @PutMapping("/disable/{ruleId}")
    public Result<Boolean> disableRule(@PathVariable Long ruleId) {
        try {
            boolean success = workflowRuleService.disableRule(ruleId);
            return Result.success(success);
        } catch (Exception e) {
            log.error("停用规则失败", e);
            return Result.error("停用规则失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除规则
     */
    @DeleteMapping("/delete/{ruleId}")
    public Result<Boolean> deleteRule(@PathVariable Long ruleId) {
        try {
            boolean success = workflowRuleService.deleteRule(ruleId);
            return Result.success(success);
        } catch (Exception e) {
            log.error("删除规则失败", e);
            return Result.error("删除规则失败：" + e.getMessage());
        }
    }
}
