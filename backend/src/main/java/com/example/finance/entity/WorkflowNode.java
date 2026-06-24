package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程节点实体类
 */
@Data
@TableName("workflow_node")
public class WorkflowNode {
    
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 流程定义 ID
     */
    @com.baomidou.mybatisplus.annotation.TableField("definition_id")
    private Long processId;
    
    /**
     * 节点标识
     */
    @com.baomidou.mybatisplus.annotation.TableField("node_key")
    private String nodeKey;
    
    /**
     * 节点名称
     */
    @com.baomidou.mybatisplus.annotation.TableField("node_name")
    private String nodeName;
    
    /**
     * 节点类型（START, USER_TASK, APPROVAL, END）
     */
    @com.baomidou.mybatisplus.annotation.TableField("node_type")
    private String nodeType;
    
    /**
     * 审批角色（DEPT_MANAGER, FINANCE_MANAGER 等）
     */
    @com.baomidou.mybatisplus.annotation.TableField("approver_role")
    private String approverRole;
    
    /**
     * 下一节点标识
     */
    @com.baomidou.mybatisplus.annotation.TableField("next_node_key")
    private String nextNodeKey;
    
    /**
     * 排序
     */
    @com.baomidou.mybatisplus.annotation.TableField("sort_order")
    private Integer sortOrder;
    
    /**
     * 处理人类型（非数据库字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String assigneeType;
    
    /**
     * 处理人值（非数据库字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String assigneeValue;
}
