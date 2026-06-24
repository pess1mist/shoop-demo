package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.WorkflowDefinition;
import com.example.finance.entity.WorkflowInstance;
import com.example.finance.entity.WorkflowTask;
import com.example.finance.mapper.WorkflowInstanceMapper;
import com.example.finance.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private com.example.finance.mapper.WorkflowInstanceMapper workflowInstanceMapper;
    
    /* ========== 流程定义管理 ========== */
    
    @GetMapping("/definition/list")
    public Result<List<WorkflowDefinition>> listDefinitions() {
        try {
            List<WorkflowDefinition> list = workflowService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询流程定义失败", e);
            return Result.error("查询流程定义失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/definition/page")
    public Result<Page<WorkflowDefinition>> pageDefinitions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<WorkflowDefinition> result = workflowService.pageDefinitions(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询流程定义失败", e);
            return Result.error("分页查询流程定义失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/definition/key/{processKey}")
    public Result<WorkflowDefinition> getLatestDefinition(@PathVariable String processKey) {
        try {
            WorkflowDefinition definition = workflowService.getLatestVersion(processKey);
            if (definition == null) {
                return Result.error("流程定义不存在");
            }
            return Result.success(definition);
        } catch (Exception e) {
            log.error("查询流程定义失败", e);
            return Result.error("查询流程定义失败：" + e.getMessage());
        }
    }
    
    /* ========== 流程实例管理 ========== */
    
    @PostMapping("/instance/start")
    public Result<Long> startProcess(
            @RequestParam String processKey,
            @RequestParam String businessType,
            @RequestParam String businessKey,
            @RequestParam Long userId,
            @RequestParam String userName) {
        try {
            Long instanceId = workflowService.startProcess(processKey, businessType, businessKey, userId, userName);
            return Result.success(instanceId);
        } catch (Exception e) {
            log.error("启动流程失败", e);
            return Result.error("启动流程失败：" + e.getMessage());
        }
    }
    
    /**
     * 启动流程（支持业务数据，用于规则匹配）
     */
    @PostMapping("/instance/start-with-data")
    public Result<Long> startProcessWithData(
            @RequestParam String processKey,
            @RequestParam String businessType,
            @RequestParam String businessKey,
            @RequestParam Long userId,
            @RequestParam String userName,
            @RequestParam(required = false) java.math.BigDecimal businessData) {
        try {
            Long instanceId = workflowService.startProcessWithData(processKey, businessType, businessKey, userId, userName, businessData);
            return Result.success(instanceId);
        } catch (Exception e) {
            log.error("启动流程失败", e);
            return Result.error("启动流程失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/instance/history/{businessType}/{businessKey}")
    public Result<List<WorkflowInstance>> getProcessHistory(
            @PathVariable String businessType,
            @PathVariable String businessKey) {
        try {
            List<WorkflowInstance> history = workflowService.getProcessHistory(businessType, businessKey);
            return Result.success(history);
        } catch (Exception e) {
            log.error("查询流程历史失败", e);
            return Result.error("查询流程历史失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询所有流程实例（分页）
     */
    @GetMapping("/instance/page")
    public Result<Page<WorkflowInstance>> pageInstances(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String businessKey) {
        try {
            Page<WorkflowInstance> result = workflowService.pageInstances(pageNum, pageSize, status, startTime, endTime, businessKey);
            // 为每个实例添加流程名称
            if (result != null && result.getRecords() != null) {
                for (WorkflowInstance instance : result.getRecords()) {
                    try {
                        WorkflowDefinition definition = workflowService.getById(instance.getProcessId());
                        if (definition != null) {
                            instance.setProcessName(definition.getProcessName());
                        }
                    } catch (Exception e) {
                        log.warn("查询流程定义失败，processId: {}", instance.getProcessId());
                    }
                }
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询流程实例失败", e);
            return Result.error("分页查询流程实例失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询流程实例详情
     */
    @GetMapping("/instance/{instanceId}")
    public Result<WorkflowInstance> getInstance(@PathVariable Long instanceId) {
        try {
            WorkflowInstance instance = workflowInstanceMapper.selectById(instanceId);
            if (instance == null) {
                return Result.error("流程实例不存在");
            }
            // 添加流程名称
            WorkflowDefinition definition = workflowService.getById(instance.getProcessId());
            if (definition != null) {
                instance.setProcessName(definition.getProcessName());
            }
            return Result.success(instance);
        } catch (Exception e) {
            log.error("查询流程实例失败", e);
            return Result.error("查询流程实例失败：" + e.getMessage());
        }
    }
    
    /**
     * 终止流程实例
     */
    @PutMapping("/instance/terminate")
    public Result<Boolean> terminateInstance(@RequestParam Long instanceId) {
        try {
            boolean result = workflowService.terminateProcess(instanceId);
            if (!result) {
                return Result.error("终止流程失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("终止流程失败", e);
            return Result.error("终止流程失败：" + e.getMessage());
        }
    }
    
    /**
     * 流程实例统计
     */
    @GetMapping("/instance/statistics")
    public Result<java.util.Map<String, Object>> getInstanceStatistics() {
        try {
            java.util.Map<String, Object> statistics = workflowService.getInstanceStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("查询流程统计失败", e);
            return Result.error("查询流程统计失败：" + e.getMessage());
        }
    }
    
    /* ========== 任务管理 ========== */
    
    @GetMapping("/task/todo/{userId}")
    public Result<List<WorkflowTask>> getTodoTasks(@PathVariable Long userId) {
        try {
            List<WorkflowTask> tasks = workflowService.getTodoTasks(userId);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("查询待办任务失败", e);
            return Result.error("查询待办任务失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/task/my/{userId}")
    public Result<List<WorkflowTask>> getMyTasks(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        try {
            List<WorkflowTask> tasks = workflowService.getMyTasks(userId, status);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("查询我的任务失败", e);
            return Result.error("查询我的任务失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/task/complete")
    public Result<Boolean> completeTask(
            @RequestParam Long taskId,
            @RequestParam String action,
            @RequestParam(required = false) String opinion,
            @RequestParam Long userId,
            @RequestParam String userName) {
        try {
            boolean result = workflowService.completeTask(taskId, action, opinion, userId, userName);
            if (!result) {
                return Result.error("处理任务失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("处理任务失败", e);
            return Result.error("处理任务失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/task/current/{instanceId}")
    public Result<WorkflowTask> getCurrentTask(@PathVariable Long instanceId) {
        try {
            WorkflowTask task = workflowService.getCurrentTask(instanceId);
            return Result.success(task);
        } catch (Exception e) {
            log.error("查询当前任务失败", e);
            return Result.error("查询当前任务失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询流程实例的任务列表
     */
    @GetMapping("/task/list/{instanceId}")
    public Result<List<WorkflowTask>> getTasksByInstance(@PathVariable Long instanceId) {
        try {
            List<WorkflowTask> tasks = workflowService.getTasksByInstanceId(instanceId);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("查询任务列表失败", e);
            return Result.error("查询任务列表失败：" + e.getMessage());
        }
    }
}
