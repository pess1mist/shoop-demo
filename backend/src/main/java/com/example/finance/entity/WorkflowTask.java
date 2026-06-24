package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程任务实体类
 */
@Data
@TableName("workflow_task")
public class WorkflowTask {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 流程实例 ID
     */
    private Long instanceId;
    
    /**
     * 流程节点标识
     */
    @com.baomidou.mybatisplus.annotation.TableField("node_key")
    private String nodeKey;
    
    /**
     * 节点名称
     */
    @com.baomidou.mybatisplus.annotation.TableField("node_name")
    private String nodeName;
    
    /**
     * 处理人 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("assignee_id")
    private Long assigneeId;
    
    /**
     * 处理人姓名
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String assigneeName;
    
    /**
     * 状态（PENDING-待处理，PROCESSING-处理中，APPROVED-已批准，REJECTED-已驳回）
     */
    private String status;
    
    /**
     * 操作（APPROVE-通过，REJECT-驳回，TRANSFER-转办）
     */
    private String action;
    
    /**
     * 审批意见
     */
    @com.baomidou.mybatisplus.annotation.TableField("comment")
    private String comment;
    
    /**
     * 创建时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("start_time")
    private LocalDateTime startTime;
    
    /**
     * 完成时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("end_time")
    private LocalDateTime endTime;
    
    /**
     * 任务创建时间（非数据库字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private LocalDateTime createdTime;
}
