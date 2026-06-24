USE test;

-- Insert workflow instances (Purchase Plan)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(4, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401001', 'Raw Material Purchase - Steel 50 tons', 1, 'Zhang San', 'COMPLETED', 'END', '2024-01-15 09:30:00', '2024-01-16 14:20:00'),
(4, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401002', 'Equipment Parts Purchase', 2, 'Li Si', 'RUNNING', 'FINANCE_APPROVE', '2024-01-18 10:15:00', NULL),
(4, 'PURCHASE_PLAN', 'PURCHASE', 'PO202402001', 'Office Supplies Purchase', 3, 'Wang Wu', 'RUNNING', 'DEPT_APPROVE', '2024-02-05 11:00:00', NULL);

-- Insert workflow instances (Budget Adjustment)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(5, 'BUDGET_ADJUST', 'BUDGET', 'BA202401001', 'Q1 Production Budget Adjustment', 1, 'Zhang San', 'COMPLETED', 'END', '2024-01-20 14:00:00', '2024-01-22 16:30:00'),
(5, 'BUDGET_ADJUST', 'BUDGET', 'BA202402001', 'Marketing Budget Addition', 4, 'Zhao Liu', 'RUNNING', 'GM_APPROVE', '2024-02-10 09:45:00', NULL);

-- Insert workflow instances (Alert Work Order)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(6, 'ALERT_WORKORDER', 'ALERT', 'AW202401001', 'Cost Over Budget Alert', 5, 'System', 'COMPLETED', 'CLOSE', '2024-01-25 08:00:00', '2024-01-25 17:00:00'),
(6, 'ALERT_WORKORDER', 'ALERT', 'AW202402001', 'Price Anomaly Alert', 5, 'System', 'RUNNING', 'PROCESS', '2024-02-15 10:30:00', NULL);

-- Insert workflow tasks (Purchase Plan - Completed)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(1, 'SUBMIT', 'Submit Application', 1, 'Zhang San', 'COMPLETED', 'SUBMIT', 'Submit purchase request', '2024-01-15 09:30:00', '2024-01-15 09:30:00'),
(1, 'DEPT_APPROVE', 'Department Manager Approval', 6, 'Sun Qi', 'COMPLETED', 'APPROVE', 'Approved, reasonable price', '2024-01-15 14:00:00', '2024-01-15 15:30:00'),
(1, 'FINANCE_APPROVE', 'Finance Approval', 7, 'Zhou Ba', 'COMPLETED', 'APPROVE', 'Budget sufficient, approved', '2024-01-16 10:00:00', '2024-01-16 14:20:00');

-- Insert workflow tasks (Purchase Plan - In Progress)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(2, 'SUBMIT', 'Submit Application', 2, 'Li Si', 'COMPLETED', 'SUBMIT', 'Urgent purchase request', '2024-01-18 10:15:00', '2024-01-18 10:15:00'),
(2, 'DEPT_APPROVE', 'Department Manager Approval', 6, 'Sun Qi', 'COMPLETED', 'APPROVE', 'Approved', '2024-01-18 15:00:00', '2024-01-18 16:00:00'),
(2, 'FINANCE_APPROVE', 'Finance Approval', 7, 'Zhou Ba', 'PENDING', NULL, NULL, '2024-01-19 09:00:00', NULL);

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(3, 'SUBMIT', 'Submit Application', 3, 'Wang Wu', 'COMPLETED', 'SUBMIT', 'Regular office supplies', '2024-02-05 11:00:00', '2024-02-05 11:00:00'),
(3, 'DEPT_APPROVE', 'Department Manager Approval', 6, 'Sun Qi', 'PENDING', NULL, NULL, '2024-02-05 14:00:00', NULL);

-- Insert workflow tasks (Budget Adjustment)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(4, 'SUBMIT', 'Submit Application', 1, 'Zhang San', 'COMPLETED', 'SUBMIT', 'Budget adjustment due to market changes', '2024-01-20 14:00:00', '2024-01-20 14:00:00'),
(4, 'FINANCE_APPROVE', 'Finance Approval', 7, 'Zhou Ba', 'COMPLETED', 'APPROVE', 'Adjustment is reasonable', '2024-01-21 10:00:00', '2024-01-21 11:30:00'),
(4, 'GM_APPROVE', 'General Manager Approval', 8, 'Wu Jiu', 'COMPLETED', 'APPROVE', 'Approved', '2024-01-22 15:00:00', '2024-01-22 16:30:00');

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(5, 'SUBMIT', 'Submit Application', 4, 'Zhao Liu', 'COMPLETED', 'SUBMIT', 'Marketing campaign needs additional budget', '2024-02-10 09:45:00', '2024-02-10 09:45:00'),
(5, 'FINANCE_APPROVE', 'Finance Approval', 7, 'Zhou Ba', 'COMPLETED', 'APPROVE', 'Additional budget approved', '2024-02-11 10:00:00', '2024-02-11 11:00:00'),
(5, 'GM_APPROVE', 'General Manager Approval', 8, 'Wu Jiu', 'PENDING', NULL, NULL, '2024-02-12 09:00:00', NULL);

-- Insert workflow tasks (Alert Work Order)
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(6, 'CREATE', 'Create Work Order', 5, 'System', 'COMPLETED', 'CREATE', 'Auto-created by system', '2024-01-25 08:00:00', '2024-01-25 08:00:00'),
(6, 'ASSIGN', 'Assign Handler', 9, 'Zheng Shi', 'COMPLETED', 'ASSIGN', 'Assigned to cost analyst', '2024-01-25 08:30:00', '2024-01-25 09:00:00'),
(6, 'PROCESS', 'Process Work Order', 10, 'Chen Shiyi', 'COMPLETED', 'PROCESS', 'Analysis: raw material price increase', '2024-01-25 10:00:00', '2024-01-25 15:00:00'),
(6, 'RESOLVE', 'Resolve Confirmation', 5, 'System', 'COMPLETED', 'APPROVE', 'Resolved', '2024-01-25 16:00:00', '2024-01-25 16:30:00'),
(6, 'CLOSE', 'Close Work Order', 5, 'System', 'COMPLETED', 'CLOSE', 'Work order closed', '2024-01-25 17:00:00', '2024-01-25 17:00:00');

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(7, 'CREATE', 'Create Work Order', 5, 'System', 'COMPLETED', 'CREATE', 'Price anomaly detected', '2024-02-15 10:30:00', '2024-02-15 10:30:00'),
(7, 'ASSIGN', 'Assign Handler', 9, 'Zheng Shi', 'COMPLETED', 'ASSIGN', 'Assigned to procurement specialist', '2024-02-15 11:00:00', '2024-02-15 11:30:00'),
(7, 'PROCESS', 'Process Work Order', 11, 'Liu Shier', 'PENDING', NULL, NULL, '2024-02-15 14:00:00', NULL);
