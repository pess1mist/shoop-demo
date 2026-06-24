package com.example.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.WorkflowDefinition;
import com.example.finance.entity.WorkflowInstance;
import com.example.finance.entity.WorkflowTask;

import java.util.List;

/**
 * 工作流 Service 接口
 */
public interface WorkflowService extends IService<WorkflowDefinition> {
    
    /**
     * 查询所有流程定义
     */
    List<WorkflowDefinition> listAll();
    
    /**
     * 分页查询流程定义
     */
    Page<WorkflowDefinition> pageDefinitions(Integer pageNum, Integer pageSize);
    
    /**
     * 根据流程标识查询
     */
    List<WorkflowDefinition> listByProcessKey(String processKey);
    
    /**
     * 获取最新版本的流程定义
     */
    WorkflowDefinition getLatestVersion(String processKey);
    
    /**
     * 发起流程实例
     * @param processKey 流程标识
     * @param businessType 业务类型
     * @param businessKey 业务主键
     * @param userId 发起人 ID
     * @param userName 发起人姓名
     * @return 流程实例 ID
     */
    Long startProcess(String processKey, String businessType, String businessKey, Long userId, String userName);
    
    /**
     * 发起流程实例（支持业务数据，用于规则匹配）
     * @param processKey 流程标识
     * @param businessType 业务类型
     * @param businessKey 业务主键
     * @param userId 发起人 ID
     * @param userName 发起人姓名
     * @param businessData 业务数据（如金额），用于规则匹配
     * @return 流程实例 ID
     */
    Long startProcessWithData(String processKey, String businessType, String businessKey, Long userId, String userName, java.math.BigDecimal businessData);
    
    /**
     * 获取待办任务列表
     */
    List<WorkflowTask> getTodoTasks(Long userId);
    
    /**
     * 获取我的任务（包括已处理）
     */
    List<WorkflowTask> getMyTasks(Long userId, String status);
    
    /**
     * 处理任务
     * @param taskId 任务 ID
     * @param action 操作（APPROVE/REJECT）
     * @param opinion 审批意见
     * @param userId 处理人 ID
     * @param userName 处理人姓名
     * @return 是否成功
     */
    boolean completeTask(Long taskId, String action, String opinion, Long userId, String userName);
    
    /**
     * 获取流程历史
     */
    List<WorkflowInstance> getProcessHistory(String businessType, String businessKey);
    
    /**
     * 获取当前任务
     */
    WorkflowTask getCurrentTask(Long instanceId);
    
    /**
     * 分页查询流程实例
     */
    Page<WorkflowInstance> pageInstances(Integer pageNum, Integer pageSize, String status, String startTime, String endTime, String businessKey);
    
    /**
     * 根据 ID 查询流程实例
     */
    WorkflowInstance getInstanceById(Long instanceId);
    
    /**
     * 终止流程实例
     */
    boolean terminateProcess(Long instanceId);
    
    /**
     * 获取流程实例统计信息
     */
    java.util.Map<String, Object> getInstanceStatistics();
    
    /**
     * 获取流程实例的任务列表
     */
    List<WorkflowTask> getTasksByInstanceId(Long instanceId);
}
