package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录审计实体类
 * 用于记录所有审批操作的详细日志
 */
@Data
@TableName("approval_log")
public class ApprovalLog {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 流程实例ID
     */
    private Long instanceId;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
    
    /**
     * 操作类型：APPROVE/REJECT/TRANSFER/DELEGATE/ADD_SIGN/SUBMIT
     */
    private String action;
    
    /**
     * 审批意见
     */
    private String opinion;
    
    /**
     * 附件URL列表，JSON格式
     */
    private String attachmentUrls;
    
    /**
     * 转交/委托来源用户ID
     */
    private Long fromUserId;
    
    /**
     * 转交/委托目标用户ID
     */
    private Long toUserId;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 处理耗时（秒）
     */
    private Integer durationSeconds;
    
    /**
     * 操作IP地址
     */
    private String ipAddress;
}
