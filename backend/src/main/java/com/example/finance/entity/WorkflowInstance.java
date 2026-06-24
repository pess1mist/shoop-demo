package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例实体类
 */
@Data
@TableName("workflow_instance")
public class WorkflowInstance {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 流程定义 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("process_id")
    private Long processId;
    
    /**
     * 流程标识（PURCHASE_PLAN, BUDGET_ADJUST 等）
     */
    @com.baomidou.mybatisplus.annotation.TableField("process_key")
    private String processKey;
    
    /**
     * 业务主键
     */
    private String businessKey;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 流程标题
     */
    private String title;
    
    /**
     * 发起人 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("initiator_id")
    private Long initiatorId;
    
    /**
     * 发起人姓名
     */
    @com.baomidou.mybatisplus.annotation.TableField("initiator_name")
    private String initiatorName;
    
    /**
     * 当前节点标识
     */
    @com.baomidou.mybatisplus.annotation.TableField("current_node_key")
    private String currentNodeKey;
    
    /**
     * 当前节点 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("current_node_id")
    private Long currentNodeId;
    
    /**
     * 状态（RUNNING-进行中，COMPLETED-已完成，TERMINATED-已终止）
     */
    private String status;
    
    /**
     * 发起人 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("started_by")
    private Long startedBy;
    
    /**
     * 发起时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("started_time")
    private LocalDateTime startedTime;
    
    /**
     * 结束时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("ended_time")
    private LocalDateTime endedTime;
    
    /**
     * 流程开始时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("start_time")
    private LocalDateTime startTime;
    
    /**
     * 流程结束时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("end_time")
    private LocalDateTime endTime;
    
    /**
     * 创建时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("created_time")
    private LocalDateTime createdTime;
    
    /**
     * 流程名称（非数据库字段，仅用于返回）
     */
    @TableField(exist = false)
    private String processName;
}
