-- Add indexes to improve query performance (Fixed version)
USE test;

-- Purchase Order Indexes
ALTER TABLE purchase_order ADD INDEX idx_order_date (order_date);
ALTER TABLE purchase_order ADD INDEX idx_supplier_id (supplier_id);
ALTER TABLE purchase_order ADD INDEX idx_approval_status (approval_status);

-- Production Order Indexes (using actual field names)
ALTER TABLE production_order ADD INDEX idx_product_code (product_code(50));
ALTER TABLE production_order ADD INDEX idx_start_date (start_date(50));

-- Budget Execution Indexes
ALTER TABLE budget_execution ADD INDEX idx_department (department(50));
ALTER TABLE budget_execution ADD INDEX idx_month (month(20));
ALTER TABLE budget_execution ADD INDEX idx_budget_type (budget_type(50));

-- Manufacturing Cost Detail Indexes
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_product_code (product_code(50));
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_cost_month (cost_month(20));
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_cost_type (cost_type(50));

-- Internal Control Log Indexes
ALTER TABLE internal_control_log ADD INDEX idx_department (department(50));
ALTER TABLE internal_control_log ADD INDEX idx_level (level(20));
ALTER TABLE internal_control_log ADD INDEX idx_status (status(20));

-- Workflow Instance Indexes
ALTER TABLE workflow_instance ADD INDEX idx_business_key (business_key(50));
ALTER TABLE workflow_instance ADD INDEX idx_business_type (business_type(50));
ALTER TABLE workflow_instance ADD INDEX idx_status (status(20));
ALTER TABLE workflow_instance ADD INDEX idx_started_by (started_by);

-- Workflow Task Indexes
ALTER TABLE workflow_task ADD INDEX idx_instance_id (instance_id);
ALTER TABLE workflow_task ADD INDEX idx_assignee (assignee);
ALTER TABLE workflow_task ADD INDEX idx_status (status(20));

-- Verify indexes added
SELECT 'Indexes added successfully!' AS result;

-- Show index summary
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) as COLUMNS
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'test'
AND TABLE_NAME IN ('purchase_order', 'production_order', 'budget_execution')
GROUP BY TABLE_NAME, INDEX_NAME;
