USE test;

-- 1. Workflow Definition Table
CREATE TABLE IF NOT EXISTS workflow_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_key VARCHAR(50) NOT NULL,
    process_name VARCHAR(100) NOT NULL,
    version INT DEFAULT 1,
    nodes TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Workflow Node Table
CREATE TABLE IF NOT EXISTS workflow_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    definition_id BIGINT NOT NULL,
    node_key VARCHAR(50) NOT NULL,
    node_name VARCHAR(100) NOT NULL,
    node_type VARCHAR(20) NOT NULL,
    approver_role VARCHAR(50),
    next_node_key VARCHAR(50),
    sort_order INT DEFAULT 0
);

-- 3. Workflow Instance Table
CREATE TABLE IF NOT EXISTS workflow_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_id BIGINT NOT NULL,
    process_key VARCHAR(50) NOT NULL,
    business_type VARCHAR(50),
    business_key VARCHAR(100),
    title VARCHAR(200),
    initiator_id BIGINT,
    initiator_name VARCHAR(50),
    status VARCHAR(20) DEFAULT 'RUNNING',
    current_node_key VARCHAR(50),
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. Workflow Task Table
CREATE TABLE IF NOT EXISTS workflow_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instance_id BIGINT NOT NULL,
    node_key VARCHAR(50) NOT NULL,
    node_name VARCHAR(100),
    assignee_id BIGINT,
    assignee_name VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    action VARCHAR(20),
    comment TEXT,
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insert workflow definitions
INSERT INTO workflow_definition (process_key, process_name, version, nodes, status) VALUES
('PURCHASE_PLAN', 'Purchase Plan Approval', 1, 'SUBMIT,DEPT_APPROVE,FINANCE_APPROVE,END', 'ACTIVE'),
('BUDGET_ADJUST', 'Budget Adjustment Approval', 1, 'SUBMIT,FINANCE_APPROVE,GM_APPROVE,END', 'ACTIVE'),
('ALERT_WORKORDER', 'Alert Work Order Processing', 1, 'CREATE,ASSIGN,PROCESS,RESOLVE,CLOSE', 'ACTIVE');

-- Insert purchase plan workflow nodes
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(1, 'SUBMIT', 'Submit Application', 'START', NULL, 'DEPT_APPROVE', 1),
(1, 'DEPT_APPROVE', 'Department Manager Approval', 'APPROVAL', 'DEPT_MANAGER', 'FINANCE_APPROVE', 2),
(1, 'FINANCE_APPROVE', 'Finance Approval', 'APPROVAL', 'FINANCE_MANAGER', 'END', 3),
(1, 'END', 'End', 'END', NULL, NULL, 4);

-- Insert budget adjustment workflow nodes
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(2, 'SUBMIT', 'Submit Application', 'START', NULL, 'FINANCE_APPROVE', 1),
(2, 'FINANCE_APPROVE', 'Finance Approval', 'APPROVAL', 'FINANCE_MANAGER', 'GM_APPROVE', 2),
(2, 'GM_APPROVE', 'General Manager Approval', 'APPROVAL', 'GENERAL_MANAGER', 'END', 3),
(2, 'END', 'End', 'END', NULL, NULL, 4);

-- Insert alert work order workflow nodes
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(3, 'CREATE', 'Create Work Order', 'START', NULL, 'ASSIGN', 1),
(3, 'ASSIGN', 'Assign Handler', 'ASSIGN', 'ADMIN', 'PROCESS', 2),
(3, 'PROCESS', 'Process Work Order', 'PROCESS', 'HANDLER', 'RESOLVE', 3),
(3, 'RESOLVE', 'Resolve Confirmation', 'APPROVAL', 'CREATOR', 'CLOSE', 4),
(3, 'CLOSE', 'Close Work Order', 'END', NULL, NULL, 5);

-- Insert workflow instances (Purchase Plan)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401001', 'Raw Material Purchase - Steel 50 tons', 1, 'Zhang San', 'COMPLETED', 'END', '2024-01-15 09:30:00', '2024-01-16 14:20:00'),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401002', 'Equipment Parts Purchase', 2, 'Li Si', 'RUNNING', 'FINANCE_APPROVE', '2024-01-18 10:15:00', NULL),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202402001', 'Office Supplies Purchase', 3, 'Wang Wu', 'RUNNING', 'DEPT_APPROVE', '2024-02-05 11:00:00', NULL);

-- Insert workflow instances (Budget Adjustment)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA202401001', 'Q1 Production Budget Adjustment', 1, 'Zhang San', 'COMPLETED', 'END', '2024-01-20 14:00:00', '2024-01-22 16:30:00'),
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA202402001', 'Marketing Budget Addition', 4, 'Zhao Liu', 'RUNNING', 'GM_APPROVE', '2024-02-10 09:45:00', NULL);

-- Insert workflow instances (Alert Work Order)
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time, end_time) VALUES
(3, 'ALERT_WORKORDER', 'ALERT', 'AW202401001', 'Cost Over Budget Alert', 5, 'System', 'COMPLETED', 'CLOSE', '2024-01-25 08:00:00', '2024-01-25 17:00:00'),
(3, 'ALERT_WORKORDER', 'ALERT', 'AW202402001', 'Price Anomaly Alert', 5, 'System', 'RUNNING', 'PROCESS', '2024-02-15 10:30:00', NULL);

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
