-- Add indexes to improve query performance
USE test;

-- Purchase Order Indexes
ALTER TABLE purchase_order ADD INDEX idx_order_date (order_date);
ALTER TABLE purchase_order ADD INDEX idx_supplier_id (supplier_id);
ALTER TABLE purchase_order ADD INDEX idx_approval_status (approval_status);

-- Production Order Indexes
ALTER TABLE production_order ADD INDEX idx_order_date (order_date);
ALTER TABLE production_order ADD INDEX idx_product_code (product_code);
ALTER TABLE production_order ADD INDEX idx_status (status);

-- Budget Execution Indexes
ALTER TABLE budget_execution ADD INDEX idx_department (department);
ALTER TABLE budget_execution ADD INDEX idx_month (month);
ALTER TABLE budget_execution ADD INDEX idx_budget_type (budget_type);

-- Manufacturing Cost Detail Indexes
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_product_code (product_code);
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_cost_month (cost_month);
ALTER TABLE manufacturing_cost_detail ADD INDEX idx_cost_type (cost_type);

-- Internal Control Log Indexes
ALTER TABLE internal_control_log ADD INDEX idx_department (department);
ALTER TABLE internal_control_log ADD INDEX idx_level (level);
ALTER TABLE internal_control_log ADD INDEX idx_status (status);

-- Workflow Instance Indexes
ALTER TABLE workflow_instance ADD INDEX idx_business_key (business_key);
ALTER TABLE workflow_instance ADD INDEX idx_business_type (business_type);
ALTER TABLE workflow_instance ADD INDEX idx_status (status);
ALTER TABLE workflow_instance ADD INDEX idx_started_by (started_by);

-- Workflow Task Indexes
ALTER TABLE workflow_task ADD INDEX idx_instance_id (instance_id);
ALTER TABLE workflow_task ADD INDEX idx_assignee (assignee);
ALTER TABLE workflow_task ADD INDEX idx_status (status);

-- Show all indexes
SELECT 'Indexes added successfully!' AS result;

SHOW INDEX FROM purchase_order;
SHOW INDEX FROM production_order;
SHOW INDEX FROM budget_execution;
