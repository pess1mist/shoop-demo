-- 创建审批规则表
CREATE TABLE IF NOT EXISTS `workflow_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(200) NOT NULL COMMENT '规则名称',
  `rule_type` varchar(20) NOT NULL COMMENT '规则类型：AMOUNT-金额阈值, CONDITION-条件分支',
  `process_key` varchar(100) NOT NULL COMMENT '流程标识（触发此规则的流程）',
  `condition_expr` text COMMENT '条件表达式JSON格式',
  `target_process_key` varchar(100) COMMENT '目标流程标识（条件分支时使用）',
  `target_node_key` varchar(100) COMMENT '目标节点标识（跳转到指定节点）',
  `threshold_amount` decimal(18,2) COMMENT '金额阈值',
  `priority` int DEFAULT '0' COMMENT '优先级，数字越大优先级越高',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用, INACTIVE-停用',
  `description` varchar(500) COMMENT '规则描述',
  `created_by` bigint COMMENT '创建人ID',
  `created_by_name` varchar(50) COMMENT '创建人姓名',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_process_key` (`process_key`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批规则表';
