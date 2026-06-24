package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程定义实体类
 */
@Data
@TableName("workflow_definition")
public class WorkflowDefinition {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 流程标识
     */
    @com.baomidou.mybatisplus.annotation.TableField("process_key")
    private String processKey;
    
    /**
     * 流程名称
     */
    @com.baomidou.mybatisplus.annotation.TableField("process_name")
    private String processName;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 流程节点配置（JSON格式）
     */
    private String nodes;
    
    /**
     * 状态（ACTIVE-启用，INACTIVE-停用）
     */
    private String status;
    
    /**
     * 创建时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("created_time")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @com.baomidou.mybatisplus.annotation.TableField("updated_time")
    private LocalDateTime updatedTime;
    
    /**
     * 流程描述（非数据库字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String description;
}
