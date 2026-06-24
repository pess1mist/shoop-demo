package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.WorkflowDefinition;
import com.example.finance.entity.WorkflowInstance;
import com.example.finance.entity.WorkflowNode;
import com.example.finance.entity.WorkflowTask;
import com.example.finance.entity.WorkflowRule;
import com.example.finance.entity.ApprovalLog;
import com.example.finance.entity.User;
import com.example.finance.entity.PurchasePlan;
import com.example.finance.mapper.WorkflowDefinitionMapper;
import com.example.finance.mapper.WorkflowInstanceMapper;
import com.example.finance.mapper.WorkflowNodeMapper;
import com.example.finance.mapper.WorkflowTaskMapper;
import com.example.finance.mapper.UserMapper;
import com.example.finance.mapper.WorkflowRuleMapper;
import com.example.finance.mapper.ApprovalLogMapper;
import com.example.finance.mapper.PurchasePlanMapper;
import com.example.finance.service.WorkflowService;
import com.example.finance.service.WorkflowRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流 Service 实现类
 */
@Slf4j
@Service
public class WorkflowServiceImpl extends ServiceImpl<WorkflowDefinitionMapper, WorkflowDefinition> implements WorkflowService {
    
    @Autowired
    private WorkflowInstanceMapper workflowInstanceMapper;
    
    @Autowired
    private WorkflowNodeMapper workflowNodeMapper;
    
    @Autowired
    private WorkflowTaskMapper workflowTaskMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private WorkflowRuleMapper workflowRuleMapper;
    
    @Autowired
    private ApprovalLogMapper approvalLogMapper;
    
    @Autowired
    private PurchasePlanMapper purchasePlanMapper;
    
    @Autowired
    private WorkflowRuleService workflowRuleService;
    
    @Override
    public List<WorkflowDefinition> listAll() {
        return this.list();
    }
    
    @Override
    public Page<WorkflowDefinition> pageDefinitions(Integer pageNum, Integer pageSize) {
        Page<WorkflowDefinition> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<WorkflowDefinition> listByProcessKey(String processKey) {
        LambdaQueryWrapper<WorkflowDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowDefinition::getProcessKey, processKey);
        return this.list(wrapper);
    }
    
    @Override
    public WorkflowDefinition getLatestVersion(String processKey) {
        List<WorkflowDefinition> definitions = this.listByProcessKey(processKey);
        if (definitions.isEmpty()) {
            return null;
        }
        // 返回版本最新的
        return definitions.stream()
            .max((d1, d2) -> d2.getVersion().compareTo(d1.getVersion()))
            .orElse(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startProcess(String processKey, String businessType, String businessKey, Long userId, String userName) {
        return startProcess(processKey, businessType, businessKey, userId, userName, null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startProcessWithData(String processKey, String businessType, String businessKey, Long userId, String userName, java.math.BigDecimal businessData) {
        return startProcess(processKey, businessType, businessKey, userId, userName, businessData);
    }
    
    /**
     * 启动流程（支持业务数据参数）
     * @param processKey 流程标识
     * @param businessType 业务类型
     * @param businessKey 业务键
     * @param userId 用户ID
     * @param userName 用户名
     * @param businessData 业务数据（如金额），用于规则匹配
     * @return 流程实例ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long startProcess(String processKey, String businessType, String businessKey, Long userId, String userName, java.math.BigDecimal businessData) {
        try {
            // 1. 获取流程定义
            WorkflowDefinition definition = getLatestVersion(processKey);
            if (definition == null) {
                log.error("流程定义不存在，processKey: {}", processKey);
                throw new RuntimeException("流程定义不存在：" + processKey);
            }
            
            // 2. 创建流程实例
            WorkflowInstance instance = new WorkflowInstance();
            instance.setProcessId(definition.getId());
            instance.setProcessKey(processKey);
            instance.setBusinessKey(businessKey);
            instance.setBusinessType(businessType);
            instance.setStatus("RUNNING");
            instance.setStartedBy(userId);
            instance.setStartedTime(LocalDateTime.now());
            workflowInstanceMapper.insert(instance);
            
            // 3. 获取所有节点
            LambdaQueryWrapper<WorkflowNode> nodeWrapper = new LambdaQueryWrapper<>();
            nodeWrapper.eq(WorkflowNode::getProcessId, definition.getId())
                      .orderByAsc(WorkflowNode::getSortOrder);
            List<WorkflowNode> nodes = workflowNodeMapper.selectList(nodeWrapper);
            
            if (nodes.isEmpty() || nodes.size() < 2) {
                log.error("流程节点配置错误，processKey: {}", processKey);
                throw new RuntimeException("流程节点配置错误");
            }
            
            // 4. 【新增】使用规则引擎智能选择起始审批节点
            WorkflowNode firstTaskNode;
            String matchedRuleName = null;
            
            if (businessData != null) {
                // 尝试匹配审批规则
                WorkflowRule matchedRule = workflowRuleService.matchRule(processKey, businessData);
                
                if (matchedRule != null && matchedRule.getTargetNodeKey() != null) {
                    // 找到匹配的规则，跳转到指定节点
                    String targetNodeKey = matchedRule.getTargetNodeKey();
                    firstTaskNode = nodes.stream()
                        .filter(n -> targetNodeKey.equals(n.getNodeKey()))
                        .findFirst()
                        .orElse(null);
                    
                    if (firstTaskNode != null) {
                        matchedRuleName = matchedRule.getRuleName();
                        log.info("规则匹配成功：{}, 目标节点：{}", matchedRuleName, targetNodeKey);
                    } else {
                        log.warn("规则匹配到节点 {} 但不存在，使用默认节点", targetNodeKey);
                        firstTaskNode = getDefaultFirstNode(nodes);
                    }
                } else {
                    // 没有匹配的规则，使用默认节点
                    log.info("未匹配到适用规则，使用默认审批节点");
                    firstTaskNode = getDefaultFirstNode(nodes);
                }
            } else {
                // 没有提供业务数据，使用默认节点
                firstTaskNode = getDefaultFirstNode(nodes);
            }
            
            // 5. 根据审批角色确定处理人
            Long assigneeId = determineNextAssigneeByRole(firstTaskNode.getApproverRole(), userId);
            String assigneeName = getNextAssigneeName(assigneeId);
            
            // 6. 创建第一个任务
            WorkflowTask task = new WorkflowTask();
            task.setInstanceId(instance.getId());
            task.setNodeKey(firstTaskNode.getNodeKey());
            task.setNodeName(firstTaskNode.getNodeName());
            task.setAssigneeId(assigneeId);
            task.setAssigneeName(assigneeName);
            task.setStatus("PENDING");
            task.setStartTime(LocalDateTime.now());
            workflowTaskMapper.insert(task);
            
            // 7. 更新实例当前节点
            instance.setCurrentNodeKey(firstTaskNode.getNodeKey());
            instance.setCurrentNodeId(firstTaskNode.getId());
            workflowInstanceMapper.updateById(instance);
            
            // 8. 【新增】记录启动日志
            recordApprovalLog(instance.getId(), task.getId(), userId, userName, "SUBMIT", 
                matchedRuleName != null ? "提交申请，匹配规则：" + matchedRuleName : "提交申请");
            
            log.info("流程实例启动成功，instanceId: {}, 起始节点：{}", instance.getId(), firstTaskNode.getNodeName());
            return instance.getId();
            
        } catch (Exception e) {
            log.error("启动流程失败", e);
            throw new RuntimeException("启动流程失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取默认的起始审批节点（跳过START节点）
     */
    private WorkflowNode getDefaultFirstNode(List<WorkflowNode> nodes) {
        return nodes.stream()
            .filter(n -> "APPROVAL".equals(n.getNodeType()))
            .findFirst()
            .orElse(nodes.get(1)); // 如果找不到 APPROVAL，取第二个节点
    }
    
    @Override
    public List<WorkflowTask> getTodoTasks(Long userId) {
        LambdaQueryWrapper<WorkflowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowTask::getAssigneeId, userId)
               .in(WorkflowTask::getStatus, "PENDING", "PROCESSING");
        return workflowTaskMapper.selectList(wrapper);
    }
    
    @Override
    public List<WorkflowTask> getMyTasks(Long userId, String status) {
        LambdaQueryWrapper<WorkflowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowTask::getAssigneeId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(WorkflowTask::getStatus, status);
        }
        wrapper.orderByDesc(WorkflowTask::getStartTime);
        List<WorkflowTask> tasks = workflowTaskMapper.selectList(wrapper);
        
        // 【优化】批量填充处理人姓名，避免 N+1 查询问题
        if (!tasks.isEmpty()) {
            // 收集所有需要查询的用户ID（去重）
            java.util.Set<Long> userIds = tasks.stream()
                .filter(task -> task.getAssigneeName() == null && task.getAssigneeId() != null)
                .map(WorkflowTask::getAssigneeId)
                .collect(java.util.stream.Collectors.toSet());
            
            if (!userIds.isEmpty()) {
                // 批量查询用户信息
                List<User> users = userMapper.selectBatchIds(userIds);
                java.util.Map<Long, String> userNameMap = users.stream()
                    .collect(java.util.stream.Collectors.toMap(User::getId, User::getRealName));
                
                // 填充处理人姓名
                for (WorkflowTask task : tasks) {
                    if (task.getAssigneeName() == null && task.getAssigneeId() != null) {
                        task.setAssigneeName(userNameMap.getOrDefault(task.getAssigneeId(), "未知用户"));
                    }
                }
            }
        }
        
        return tasks;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(Long taskId, String action, String opinion, Long userId, String userName) {
        try {
            // 1. 获取任务
            WorkflowTask task = workflowTaskMapper.selectById(taskId);
            if (task == null) {
                log.error("任务不存在，taskId: {}", taskId);
                return false;
            }
            
            // 2. 更新任务状态
            task.setAction(action);
            task.setComment(opinion);
            task.setEndTime(LocalDateTime.now());
            
            if ("APPROVE".equals(action)) {
                task.setStatus("APPROVED");
            } else if ("REJECT".equals(action)) {
                task.setStatus("REJECTED");
            }
            workflowTaskMapper.updateById(task);
            
            // 【新增】记录审批日志
            WorkflowInstance tempInstance = workflowInstanceMapper.selectById(task.getInstanceId());
            if (tempInstance != null) {
                recordApprovalLog(tempInstance.getId(), taskId, userId, userName, action, opinion);
            }
            
            // 3. 获取流程实例
            WorkflowInstance instance = workflowInstanceMapper.selectById(task.getInstanceId());
            if (instance == null) {
                log.error("流程实例不存在，instanceId: {}", task.getInstanceId());
                return false;
            }
            
            // 4. 如果是驳回，直接终止流程
            if ("REJECT".equals(action)) {
                instance.setStatus("TERMINATED");
                workflowInstanceMapper.updateById(instance);
                
                // 【新增】同步更新采购计划状态
                syncPurchasePlanStatus(instance, "REJECTED");
                
                log.info("流程已驳回，instanceId: {}", instance.getId());
                return true;
            }
            
            // 5. 获取下一个节点
            WorkflowNode currentNode = getAllNodes(instance.getProcessId()).stream()
                .filter(n -> n.getNodeKey().equals(task.getNodeKey()))
                .findFirst()
                .orElse(null);
            if (currentNode == null) {
                log.error("找不到当前节点，nodeKey: {}", task.getNodeKey());
                return false;
            }
            List<WorkflowNode> allNodes = getAllNodes(instance.getProcessId());
            
            int currentIndex = -1;
            for (int i = 0; i < allNodes.size(); i++) {
                if (allNodes.get(i).getNodeKey().equals(currentNode.getNodeKey())) {
                    currentIndex = i;
                    break;
                }
            }
            
            // 6. 如果没有下一个节点，流程结束
            if (currentIndex == -1 || currentIndex >= allNodes.size() - 1) {
                instance.setStatus("COMPLETED");
                instance.setEndedTime(LocalDateTime.now());
                workflowInstanceMapper.updateById(instance);
                
                // 【新增】同步更新采购计划状态
                syncPurchasePlanStatus(instance, "APPROVED");
                
                log.info("流程已完成，instanceId: {}", instance.getId());
                return true;
            }
            
            // 7. 创建下一个节点的任务
            WorkflowNode nextNode = allNodes.get(currentIndex + 1);
            
            // 如果是 END_EVENT，流程结束
            if ("END_EVENT".equals(nextNode.getNodeType())) {
                instance.setStatus("COMPLETED");
                instance.setEndedTime(LocalDateTime.now());
                workflowInstanceMapper.updateById(instance);
                
                // 【新增】同步更新采购计划状态
                syncPurchasePlanStatus(instance, "APPROVED");
                
                log.info("流程已结束，instanceId: {}", instance.getId());
                return true;
            }
            
            // 8. 确定下一个处理人
            Long nextAssigneeId = determineNextAssigneeByRole(nextNode.getApproverRole(), userId);
            String nextAssigneeName = getNextAssigneeName(nextAssigneeId);
            
            WorkflowTask nextTask = new WorkflowTask();
            nextTask.setInstanceId(instance.getId());
            nextTask.setNodeKey(nextNode.getNodeKey());
            nextTask.setNodeName(nextNode.getNodeName());
            nextTask.setAssigneeId(nextAssigneeId);
            nextTask.setAssigneeName(nextAssigneeName);
            nextTask.setStatus("PENDING");
            workflowTaskMapper.insert(nextTask);
            
            // 9. 更新实例当前节点
            instance.setCurrentNodeKey(nextNode.getNodeKey());
            instance.setCurrentNodeId(nextNode.getId());
            workflowInstanceMapper.updateById(instance);
            
            log.info("任务完成并流转到下一节点，taskId: {}, nextNodeId: {}", taskId, nextNode.getId());
            return true;
        } catch (Exception e) {
            log.error("处理任务失败", e);
            return false;
        }
    }
    
    @Override
    public List<WorkflowInstance> getProcessHistory(String businessType, String businessKey) {
        LambdaQueryWrapper<WorkflowInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowInstance::getBusinessType, businessType)
               .eq(WorkflowInstance::getBusinessKey, businessKey)
               .orderByDesc(WorkflowInstance::getStartedTime);
        return workflowInstanceMapper.selectList(wrapper);
    }
    
    @Override
    public WorkflowTask getCurrentTask(Long instanceId) {
        LambdaQueryWrapper<WorkflowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowTask::getInstanceId, instanceId)
               .in(WorkflowTask::getStatus, "PENDING", "PROCESSING")
               .orderByDesc(WorkflowTask::getStartTime);
        List<WorkflowTask> tasks = workflowTaskMapper.selectList(wrapper);
        return tasks.isEmpty() ? null : tasks.get(0);
    }
    
    /**
     * 记录审批日志
     */
    private void recordApprovalLog(Long instanceId, Long taskId, Long operatorId, String operatorName, 
                                   String action, String opinion) {
        try {
            ApprovalLog log = new ApprovalLog();
            log.setInstanceId(instanceId);
            log.setTaskId(taskId);
            log.setOperatorId(operatorId);
            log.setOperatorName(operatorName);
            log.setAction(action);
            log.setOpinion(opinion);
            log.setOperationTime(LocalDateTime.now());
            approvalLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不影响主流程
            log.error("记录审批日志失败", e);
        }
    }
    
    @Override
    public Page<WorkflowInstance> pageInstances(Integer pageNum, Integer pageSize, String status, String startTime, String endTime, String businessKey) {
        Page<WorkflowInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkflowInstance> wrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq(WorkflowInstance::getStatus, status);
        }
        
        // 开始时间筛选
        if (startTime != null && !startTime.isEmpty()) {
            try {
                java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(startTime, 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                wrapper.ge(WorkflowInstance::getStartedTime, startDateTime);
            } catch (Exception e) {
                log.warn("开始时间格式错误: {}", startTime);
            }
        }
        
        // 结束时间筛选
        if (endTime != null && !endTime.isEmpty()) {
            try {
                java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(endTime, 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                wrapper.le(WorkflowInstance::getStartedTime, endDateTime);
            } catch (Exception e) {
                log.warn("结束时间格式错误: {}", endTime);
            }
        }
        
        // 业务单号筛选
        if (businessKey != null && !businessKey.isEmpty()) {
            wrapper.like(WorkflowInstance::getBusinessKey, businessKey);
        }
        
        wrapper.orderByDesc(WorkflowInstance::getStartedTime);
        return workflowInstanceMapper.selectPage(page, wrapper);
    }
    
    @Override
    public WorkflowInstance getInstanceById(Long instanceId) {
        return workflowInstanceMapper.selectById(instanceId);
    }
    
    /**
     * 获取流程的所有节点
     */
    private List<WorkflowNode> getAllNodes(Long processId) {
        LambdaQueryWrapper<WorkflowNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowNode::getProcessId, processId)
               .orderByAsc(WorkflowNode::getSortOrder);
        return workflowNodeMapper.selectList(wrapper);
    }
    
    /**
     * 根据角色确定下一个处理人
     */
    private Long determineNextAssigneeByRole(String approverRole, Long currentUserId) {
        if (approverRole == null || approverRole.isEmpty()) {
            return currentUserId;
        }
        
        // 根据角色查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, approverRole)
               .eq(User::getStatus, "ENABLED")
               .orderByAsc(User::getId)
               .last("LIMIT 1");
        User user = userMapper.selectOne(wrapper);
        
        if (user != null) {
            log.info("找到角色 {} 的处理人: {} (ID: {})", approverRole, user.getRealName(), user.getId());
            return user.getId();
        }
        
        log.warn("未找到角色为 {} 的用户，使用当前用户作为默认值", approverRole);
        return currentUserId;
    }
    
    /**
     * 获取处理人姓名
     */
    private String getNextAssigneeName(Long assigneeId) {
        // 从用户表查询姓名
        if (assigneeId == null) {
            return "未知用户";
        }
        User user = userMapper.selectById(assigneeId);
        if (user != null && user.getRealName() != null) {
            return user.getRealName();
        }
        return "用户" + assigneeId;
    }
    
    /**
     * 同步更新采购计划状态
     * @param instance 流程实例
     * @param action 审批动作（APPROVED/REJECTED）
     */
    private void syncPurchasePlanStatus(WorkflowInstance instance, String action) {
        try {
            // 只处理采购计划类型的流程
            if (!"PURCHASE_PLAN".equals(instance.getBusinessType())) {
                return;
            }
            
            // 获取业务主键（采购计划 ID）
            String businessKey = instance.getBusinessKey();
            if (businessKey == null) {
                return;
            }
            
            // 查询采购计划
            PurchasePlan plan = purchasePlanMapper.selectById(Long.parseLong(businessKey));
            if (plan == null) {
                log.warn("采购计划不存在，businessKey: {}", businessKey);
                return;
            }
            
            // 更新采购计划状态
            if ("APPROVED".equals(action)) {
                plan.setStatus("APPROVED");
                log.info("采购计划审批通过，planId: {}, planCode: {}", plan.getId(), plan.getPlanCode());
            } else if ("REJECTED".equals(action)) {
                plan.setStatus("REJECTED");
                log.info("采购计划被驳回，planId: {}, planCode: {}", plan.getId(), plan.getPlanCode());
            }
            
            purchasePlanMapper.updateById(plan);
            
        } catch (Exception e) {
            // 同步失败不影响主流程
            log.error("同步采购计划状态失败，instanceId: {}", instance.getId(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean terminateProcess(Long instanceId) {
        try {
            WorkflowInstance instance = workflowInstanceMapper.selectById(instanceId);
            if (instance == null) {
                log.error("流程实例不存在，instanceId: {}", instanceId);
                return false;
            }
            
            // 只能终止运行中的流程
            if (!"RUNNING".equals(instance.getStatus()) && !"进行中".equals(instance.getStatus())) {
                log.warn("流程状态不允许终止，instanceId: {}, status: {}", instanceId, instance.getStatus());
                return false;
            }
            
            // 更新流程实例状态
            instance.setStatus("TERMINATED");
            instance.setEndedTime(LocalDateTime.now());
            workflowInstanceMapper.updateById(instance);
            
            // 取消所有待处理的任务
            LambdaQueryWrapper<WorkflowTask> taskWrapper = new LambdaQueryWrapper<>();
            taskWrapper.eq(WorkflowTask::getInstanceId, instanceId)
                       .in(WorkflowTask::getStatus, "PENDING", "IN_PROGRESS");
            List<WorkflowTask> tasks = workflowTaskMapper.selectList(taskWrapper);
            
            for (WorkflowTask task : tasks) {
                task.setStatus("CANCELLED");
                task.setEndTime(LocalDateTime.now());
                workflowTaskMapper.updateById(task);
            }
            
            log.info("流程终止成功，instanceId: {}, 取消任务数: {}", instanceId, tasks.size());
            return true;
            
        } catch (Exception e) {
            log.error("终止流程失败，instanceId: {}", instanceId, e);
            return false;
        }
    }
    
    @Override
    public java.util.Map<String, Object> getInstanceStatistics() {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        
        try {
            // 总数
            long total = workflowInstanceMapper.selectCount(null);
            statistics.put("total", total);
            
            // 进行中
            LambdaQueryWrapper<WorkflowInstance> runningWrapper = new LambdaQueryWrapper<>();
            runningWrapper.in(WorkflowInstance::getStatus, "RUNNING", "进行中");
            long running = workflowInstanceMapper.selectCount(runningWrapper);
            statistics.put("running", running);
            
            // 已完成
            LambdaQueryWrapper<WorkflowInstance> completedWrapper = new LambdaQueryWrapper<>();
            completedWrapper.in(WorkflowInstance::getStatus, "COMPLETED", "已完成");
            long completed = workflowInstanceMapper.selectCount(completedWrapper);
            statistics.put("completed", completed);
            
            // 待审批
            LambdaQueryWrapper<WorkflowInstance> pendingWrapper = new LambdaQueryWrapper<>();
            pendingWrapper.in(WorkflowInstance::getStatus, "PENDING", "待审批");
            long pending = workflowInstanceMapper.selectCount(pendingWrapper);
            statistics.put("pending", pending);
            
        } catch (Exception e) {
            log.error("查询流程统计失败", e);
        }
        
        return statistics;
    }
    
    @Override
    public List<WorkflowTask> getTasksByInstanceId(Long instanceId) {
        try {
            LambdaQueryWrapper<WorkflowTask> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowTask::getInstanceId, instanceId)
                   .orderByAsc(WorkflowTask::getStartTime);
            
            List<WorkflowTask> tasks = workflowTaskMapper.selectList(wrapper);
            
            // 为每个任务补充处理人姓名
            for (WorkflowTask task : tasks) {
                if (task.getAssigneeId() != null) {
                    User user = userMapper.selectById(task.getAssigneeId());
                    if (user != null) {
                        task.setAssigneeName(user.getUsername());
                    }
                }
            }
            
            return tasks;
            
        } catch (Exception e) {
            log.error("查询任务列表失败，instanceId: {}", instanceId, e);
            return new java.util.ArrayList<>();
        }
    }
}
