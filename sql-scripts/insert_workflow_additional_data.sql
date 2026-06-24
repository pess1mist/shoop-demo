-- Insert additional workflow data for userId=1 (Admin User)
USE test;

-- ============================================
-- 1. Insert additional workflow instances
-- ============================================

-- Instance 8: Purchase Plan (PENDING tasks for user 1)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(4, 'PURCHASE_PLAN', 'PURCHASE', 'PO202403001', 'IT Equipment Purchase - Computers 20 units', 1, 'Admin User', 'RUNNING', 'DEPT_APPROVE', '2024-03-10 09:00:00', NULL),
(4, 'PURCHASE_PLAN', 'PURCHASE', 'PO202403002', 'Lab Equipment Purchase', 2, 'Li Si', 'RUNNING', 'FINANCE_APPROVE', '2024-03-12 10:30:00', NULL);

-- Instance 9: Budget Adjustment (various statuses)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(5, 'BUDGET_ADJUST', 'BUDGET', 'BA202403001', 'R&D Budget Increase', 1, 'Admin User', 'RUNNING', 'FINANCE_APPROVE', '2024-03-15 14:00:00', NULL),
(5, 'BUDGET_ADJUST', 'BUDGET', 'BA202403002', 'Training Budget Adjustment', 3, 'Wang Wu', 'COMPLETED', 'END', '2024-03-01 09:00:00', '2024-03-05 16:00:00');

-- Instance 10: Alert Work Order (for user 1)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(6, 'ALERT_WORKORDER', 'ALERT', 'AW202403001', 'Supplier Price Increase Alert', 5, 'System', 'RUNNING', 'PROCESS', '2024-03-18 08:00:00', NULL),
(6, 'ALERT_WORKORDER', 'ALERT', 'AW202402002', 'Budget Execution Warning', 5, 'System', 'RUNNING', 'ASSIGN', '2024-03-20 10:00:00', NULL);

-- ============================================
-- 2. Insert workflow tasks with various statuses
-- ============================================

-- Instance 8: PENDING task for user 1 (Dept Manager role)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(8, 'SUBMIT', 'Submit Application', 1, 'Admin User', 'COMPLETED', 'SUBMIT', 'Purchase request for IT equipment', '2024-03-10 09:00:00', '2024-03-10 09:00:00'),
(8, 'DEPT_APPROVE', 'Department Manager Approval', 1, 'Admin User', 'PENDING', NULL, NULL, '2024-03-10 10:00:00', NULL);

-- Instance 9: PENDING task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(9, 'SUBMIT', 'Submit Application', 1, 'Admin User', 'COMPLETED', 'SUBMIT', 'Budget adjustment for R&D department', '2024-03-15 14:00:00', '2024-03-15 14:00:00'),
(9, 'FINANCE_APPROVE', 'Finance Approval', 1, 'Admin User', 'PENDING', NULL, NULL, '2024-03-15 15:00:00', NULL);

-- Instance 10: Alert work order with PENDING task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(10, 'CREATE', 'Create Work Order', 5, 'System', 'COMPLETED', 'CREATE', 'Auto-created by system', '2024-03-18 08:00:00', '2024-03-18 08:00:00'),
(10, 'ASSIGN', 'Assign Handler', 1, 'Admin User', 'PENDING', NULL, NULL, '2024-03-18 09:00:00', NULL);

-- Instance 11: Alert work order with PENDING task for user 1 (as assignee)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(11, 'CREATE', 'Create Work Order', 5, 'System', 'COMPLETED', 'CREATE', 'Budget warning triggered', '2024-03-20 10:00:00', '2024-03-20 10:00:00'),
(11, 'ASSIGN', 'Assign Handler', 1, 'Admin User', 'PENDING', NULL, NULL, '2024-03-20 10:30:00', NULL);

-- Instance 2: Add REJECTED task history for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(2, 'FINANCE_APPROVE', 'Finance Approval', 1, 'Admin User', 'REJECTED', 'REJECT', 'Budget insufficient, please revise', '2024-01-19 09:00:00', '2024-01-19 11:00:00');

-- Instance 3: Add APPROVED task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(3, 'DEPT_APPROVE', 'Department Manager Approval', 1, 'Admin User', 'APPROVED', 'APPROVE', 'Approved, within budget', '2024-02-05 14:00:00', '2024-02-05 16:00:00');

-- Instance 4: Add APPROVED task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(4, 'FINANCE_APPROVE', 'Finance Approval', 1, 'Admin User', 'APPROVED', 'APPROVE', 'Budget adjustment is reasonable', '2024-01-21 10:00:00', '2024-01-21 14:00:00');

-- Instance 6: Add COMPLETED task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(6, 'RESOLVE', 'Resolve Confirmation', 1, 'Admin User', 'COMPLETED', 'APPROVE', 'Issue resolved', '2024-01-25 16:00:00', '2024-01-25 17:00:00');

-- Instance 9 (BA202403002): Add COMPLETED task for user 1
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(12, 'SUBMIT', 'Submit Application', 3, 'Wang Wu', 'COMPLETED', 'SUBMIT', 'Training budget adjustment request', '2024-03-01 09:00:00', '2024-03-01 09:00:00'),
(12, 'FINANCE_APPROVE', 'Finance Approval', 1, 'Admin User', 'COMPLETED', 'APPROVE', 'Approved, budget available', '2024-03-02 10:00:00', '2024-03-02 11:00:00'),
(12, 'GM_APPROVE', 'General Manager Approval', 1, 'Admin User', 'COMPLETED', 'APPROVE', 'Final approval granted', '2024-03-05 14:00:00', '2024-03-05 16:00:00');

-- ============================================
-- 3. Verify data
-- ============================================
SELECT 'Additional workflow instances inserted:' AS info, COUNT(*) AS count 
FROM workflow_instance WHERE id > 7;

SELECT 'Additional workflow tasks inserted:' AS info, COUNT(*) AS count 
FROM workflow_task WHERE id > (SELECT MAX(id) FROM workflow_task WHERE instance_id <= 7);

-- Show all tasks for user 1
SELECT 
    t.id,
    t.instance_id,
    t.node_name AS task_name,
    t.assignee_name,
    t.status,
    t.action,
    t.comment,
    t.start_time,
    t.end_time
FROM workflow_task t
WHERE t.assignee_id = 1
ORDER BY t.start_time DESC;

SELECT 'Data insertion completed!' AS result;
