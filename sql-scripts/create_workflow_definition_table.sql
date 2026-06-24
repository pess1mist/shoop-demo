-- Create workflow_definition table
USE test;

DROP TABLE IF EXISTS `workflow_definition`;

CREATE TABLE `workflow_definition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Definition ID',
  `name` varchar(100) NOT NULL COMMENT 'Process Name',
  `code` varchar(50) NOT NULL COMMENT 'Process Code',
  `description` varchar(500) DEFAULT NULL COMMENT 'Description',
  `version` varchar(20) DEFAULT '1.0' COMMENT 'Version',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT 'Status: ACTIVE, INACTIVE',
  `created_by` bigint DEFAULT NULL COMMENT 'Creator',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Workflow Definition';

-- Insert default workflow definition
INSERT INTO workflow_definition (name, code, description, version, status) VALUES
('采购审批流程', 'PURCHASE', 'Purchase Order Approval Process', '1.0', 'ACTIVE'),
('生产审批流程', 'PRODUCTION', 'Production Order Approval Process', '1.0', 'ACTIVE');

-- Verify
SELECT 'Workflow definition table created!' AS result;
SELECT * FROM workflow_definition;
