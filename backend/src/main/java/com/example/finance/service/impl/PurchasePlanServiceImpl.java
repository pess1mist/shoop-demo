package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.*;
import com.example.finance.mapper.PurchasePlanMapper;
import com.example.finance.mapper.WorkflowInstanceMapper;
import com.example.finance.mapper.WorkflowTaskMapper;
import com.example.finance.mapper.WorkflowNodeMapper;
import com.example.finance.service.PurchaseApprovalService;
import com.example.finance.service.PurchasePlanService;
import com.example.finance.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购计划 Service 实现类
 */
@Slf4j
@Service
public class PurchasePlanServiceImpl extends ServiceImpl<PurchasePlanMapper, PurchasePlan> implements PurchasePlanService {
    
    @Autowired
    private PurchaseApprovalService purchaseApprovalService;
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private WorkflowInstanceMapper workflowInstanceMapper;
    
    @Autowired
    private WorkflowTaskMapper workflowTaskMapper;
    
    @Autowired
    private WorkflowNodeMapper workflowNodeMapper;
    
    @Override
    public List<PurchasePlan> listAll() {
        return this.list();
    }
    
    @Override
    public Page<PurchasePlan> pagePurchasePlan(Integer pageNum, Integer pageSize) {
        Page<PurchasePlan> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<PurchasePlan> listByDept(String deptCode) {
        LambdaQueryWrapper<PurchasePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchasePlan::getDeptCode, deptCode);
        return this.list(wrapper);
    }
    
    @Override
    public List<PurchasePlan> listByBudgetId(Long budgetId) {
        LambdaQueryWrapper<PurchasePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchasePlan::getBudgetId, budgetId);
        return this.list(wrapper);
    }
    
    @Override
    public List<PurchasePlan> listByStatus(String status) {
        LambdaQueryWrapper<PurchasePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchasePlan::getStatus, status);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPurchasePlan(PurchasePlan purchasePlan) {
        try {
            // 自动生成计划单号
            if (purchasePlan.getPlanCode() == null || purchasePlan.getPlanCode().isEmpty()) {
                String planCode = generatePlanCode();
                purchasePlan.setPlanCode(planCode);
                log.info("自动生成计划单号：{}", planCode);
            }
            
            purchasePlan.setStatus("DRAFT");
            purchasePlan.setCreatedTime(LocalDateTime.now());
            // 自动计算总金额
            if (purchasePlan.getQuantity() != null && purchasePlan.getUnitPrice() != null) {
                purchasePlan.setTotalAmount(purchasePlan.getUnitPrice().multiply(new BigDecimal(purchasePlan.getQuantity())));
            }
            return this.save(purchasePlan);
        } catch (Exception e) {
            log.error("创建采购计划失败", e);
            return false;
        }
    }
    
    /**
     * 生成计划单号
     * 格式：PP-YYYYMMDD-XXXX (PP + 日期 + 4位随机数)
     */
    private String generatePlanCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "PP-" + dateStr + "-" + random;
    }
    
    @Override
    public boolean submitForApproval(Long planId, Long userId, String userName) {
        try {
            PurchasePlan plan = this.getById(planId);
            if (plan == null) {
                log.error("采购计划不存在，id: {}", planId);
                return false;
            }
            
            // 启动工作流引擎（使用 REQUIRES_NEW 确保工作流在独立事务中执行）
            Long instanceId = startWorkflowForPlan(planId, userId, userName);
            
            // 更新采购计划状态
            plan.setWorkflowInstanceId(instanceId);
            plan.setStatus("PENDING");
            boolean updated = this.updateById(plan);
            
            if (updated) {
                log.info("采购计划提交审批成功，planId: {}, workflowInstanceId: {}", planId, instanceId);
            } else {
                log.error("更新采购计划状态失败，planId: {}", planId);
            }
            return updated;
        } catch (Exception e) {
            log.error("提交审批失败", e);
            return false;
        }
    }
    
    /**
     * 启动工作流（独立事务）
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Long startWorkflowForPlan(Long planId, Long userId, String userName) {
        return workflowService.startProcess(
            "PURCHASE_PLAN",      // 流程标识
            "PURCHASE_PLAN",      // 业务类型
            planId.toString(),    // 业务主键
            userId,
            userName
        );
    }
            
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approvePlan(Long planId, Long approverId, String approverName, String opinion) {
        try {
            PurchasePlan plan = this.getById(planId);
            if (plan == null) {
                log.error("采购计划不存在，id: {}", planId);
                return false;
            }
            
            plan.setStatus("APPROVED");
            this.updateById(plan);
            
            // 更新审批记录
            LambdaQueryWrapper<PurchaseApproval> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseApproval::getPlanId, planId);
            wrapper.orderByDesc(PurchaseApproval::getId);
            List<PurchaseApproval> approvals = purchaseApprovalService.list(wrapper);
            
            if (!approvals.isEmpty()) {
                PurchaseApproval latestApproval = approvals.get(0);
                latestApproval.setApprovalResult("APPROVED");
                latestApproval.setApprovalOpinion(opinion);
                latestApproval.setApprovalTime(LocalDateTime.now());
                latestApproval.setApproverId(approverId);
                latestApproval.setApproverName(approverName);
                purchaseApprovalService.updateById(latestApproval);
            }
            
            return true;
        } catch (Exception e) {
            log.error("审批通过失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectPlan(Long planId, Long approverId, String approverName, String opinion) {
        try {
            PurchasePlan plan = this.getById(planId);
            if (plan == null) {
                log.error("采购计划不存在，id: {}", planId);
                return false;
            }
            
            plan.setStatus("REJECTED");
            plan.setRemark(opinion);
            this.updateById(plan);
            
            // 更新审批记录
            LambdaQueryWrapper<PurchaseApproval> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseApproval::getPlanId, planId);
            wrapper.orderByDesc(PurchaseApproval::getId);
            List<PurchaseApproval> approvals = purchaseApprovalService.list(wrapper);
            
            if (!approvals.isEmpty()) {
                PurchaseApproval latestApproval = approvals.get(0);
                latestApproval.setApprovalResult("REJECTED");
                latestApproval.setApprovalOpinion(opinion);
                latestApproval.setApprovalTime(LocalDateTime.now());
                latestApproval.setApproverId(approverId);
                latestApproval.setApproverName(approverName);
                purchaseApprovalService.updateById(latestApproval);
            }
            
            return true;
        } catch (Exception e) {
            log.error("驳回失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completePlan(Long planId) {
        try {
            PurchasePlan plan = this.getById(planId);
            if (plan == null) {
                log.error("采购计划不存在，id: {}", planId);
                return false;
            }
            
            plan.setStatus("COMPLETED");
            return this.updateById(plan);
        } catch (Exception e) {
            log.error("完成采购失败", e);
            return false;
        }
    }
    
    @Override
    public Object getWorkflowProgress(Long planId) {
        try {
            // 获取采购计划
            PurchasePlan plan = this.getById(planId);
            if (plan == null || plan.getWorkflowInstanceId() == null) {
                return null;
            }
            
            // 获取流程实例
            WorkflowInstance instance = workflowInstanceMapper.selectById(plan.getWorkflowInstanceId());
            if (instance == null) {
                return null;
            }
            
            // 获取流程节点
            List<WorkflowNode> nodes = workflowNodeMapper.selectList(
                new LambdaQueryWrapper<WorkflowNode>()
                    .eq(WorkflowNode::getProcessId, instance.getProcessId())
                    .orderByAsc(WorkflowNode::getSortOrder)
            );
            
            // 获取所有任务
            List<WorkflowTask> tasks = workflowTaskMapper.selectList(
                new LambdaQueryWrapper<WorkflowTask>()
                    .eq(WorkflowTask::getInstanceId, instance.getId())
                    .orderByAsc(WorkflowTask::getStartTime)
            );
            
            // 构建进度信息
            Map<String, Object> progress = new HashMap<>();
            progress.put("instanceId", instance.getId());
            progress.put("status", instance.getStatus());
            progress.put("currentNodeKey", instance.getCurrentNodeKey());
            progress.put("startedTime", instance.getStartTime());
            progress.put("endedTime", instance.getEndTime());
            
            // 流程节点列表
            List<Map<String, Object>> nodeProgress = new ArrayList<>();
            for (WorkflowNode node : nodes) {
                Map<String, Object> nodeInfo = new HashMap<>();
                nodeInfo.put("nodeKey", node.getNodeKey());
                nodeInfo.put("nodeName", node.getNodeName());
                nodeInfo.put("nodeType", node.getNodeType());
                nodeInfo.put("sortOrder", node.getSortOrder());
                
                // 查找对应的任务
                WorkflowTask task = tasks.stream()
                    .filter(t -> t.getNodeKey().equals(node.getNodeKey()))
                    .findFirst()
                    .orElse(null);
                
                if (task != null) {
                    nodeInfo.put("taskStatus", task.getStatus());
                    nodeInfo.put("assigneeName", task.getAssigneeName());
                    nodeInfo.put("action", task.getAction());
                    nodeInfo.put("comment", task.getComment());
                    nodeInfo.put("startTime", task.getStartTime());
                    nodeInfo.put("endTime", task.getEndTime());
                } else {
                    nodeInfo.put("taskStatus", "PENDING");
                }
                
                nodeProgress.add(nodeInfo);
            }
            progress.put("nodes", nodeProgress);
            
            // 当前节点信息
            WorkflowNode currentNode = nodes.stream()
                .filter(n -> n.getNodeKey().equals(instance.getCurrentNodeKey()))
                .findFirst()
                .orElse(null);
            
            if (currentNode != null) {
                Map<String, Object> currentInfo = new HashMap<>();
                currentInfo.put("nodeKey", currentNode.getNodeKey());
                currentInfo.put("nodeName", currentNode.getNodeName());
                
                // 获取当前任务
                WorkflowTask currentTask = tasks.stream()
                    .filter(t -> t.getNodeKey().equals(currentNode.getNodeKey()) 
                               && "PENDING".equals(t.getStatus()))
                    .findFirst()
                    .orElse(null);
                
                if (currentTask != null) {
                    currentInfo.put("assigneeName", currentTask.getAssigneeName());
                    currentInfo.put("assigneeId", currentTask.getAssigneeId());
                }
                
                progress.put("currentNode", currentInfo);
            }
            
            return progress;
        } catch (Exception e) {
            log.error("查询审批进度失败", e);
            return null;
        }
    }
}
