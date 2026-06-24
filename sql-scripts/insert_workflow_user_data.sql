-- Insert system users and workflow data
USE test;

-- ============================================
-- 1. Insert System Users
-- ============================================
INSERT INTO sys_user (username, password, real_name, email, phone, status) VALUES
('admin', '123456', 'Admin User', 'admin@test.com', '13800138000', 1),
('zhangsan', '123456', 'Zhang San', 'zhangsan@test.com', '13800138001', 1),
('lisi', '123456', 'Li Si', 'lisi@test.com', '13800138002', 1),
('wangwu', '123456', 'Wang Wu', 'wangwu@test.com', '13800138003', 1);

-- ============================================
-- 2. Insert Workflow Definition (if exists)
-- ============================================
-- Check if workflow_definition table exists
-- If not, skip this part

-- ============================================
-- 3. Insert Workflow Instances
-- ============================================
INSERT INTO workflow_instance (process_id, business_key, business_type, current_node_id, status, started_by, started_time, ended_time) VALUES
(1, 'PO24001', 'PURCHASE', 1, 'COMPLETED', 2, '2025-01-10 09:00:00', '2025-01-12 16:00:00'),
(1, 'PO24002', 'PURCHASE', 2, 'RUNNING', 1, '2025-01-15 10:30:00', NULL),
(1, 'PO24003', 'PURCHASE', 1, 'COMPLETED', 3, '2025-02-12 11:00:00', '2025-02-14 15:30:00'),
(1, 'MO24001', 'PRODUCTION', 3, 'RUNNING', 2, '2025-01-20 14:00:00', NULL),
(1, 'MO24002', 'PRODUCTION', 2, 'PENDING', 1, '2025-02-05 09:30:00', NULL);

-- ============================================
-- 4. Insert Workflow Tasks
-- ============================================
INSERT INTO workflow_task (instance_id, node_id, task_name, assignee, status, created_time, completed_time) VALUES
-- Tasks for instance 1 (PO24001 - Completed)
(1, 1, 'Department Manager Approval', 2, 'COMPLETED', '2025-01-10 09:00:00', '2025-01-10 14:00:00'),
(1, 2, 'Finance Approval', 3, 'COMPLETED', '2025-01-10 14:00:00', '2025-01-11 10:00:00'),
(1, 3, 'General Manager Approval', 4, 'COMPLETED', '2025-01-11 10:00:00', '2025-01-12 16:00:00'),

-- Tasks for instance 2 (PO24002 - Running)
(2, 1, 'Department Manager Approval', 2, 'COMPLETED', '2025-01-15 10:30:00', '2025-01-15 16:00:00'),
(2, 2, 'Finance Approval', 3, 'PENDING', '2025-01-15 16:00:00', NULL),

-- Tasks for instance 3 (PO24003 - Completed)
(3, 1, 'Department Manager Approval', 2, 'COMPLETED', '2025-02-12 11:00:00', '2025-02-12 15:00:00'),
(3, 2, 'Finance Approval', 3, 'COMPLETED', '2025-02-12 15:00:00', '2025-02-13 11:00:00'),
(3, 3, 'General Manager Approval', 4, 'COMPLETED', '2025-02-13 11:00:00', '2025-02-14 15:30:00'),

-- Tasks for instance 4 (MO24001 - Running)
(4, 1, 'Production Plan Review', 2, 'COMPLETED', '2025-01-20 14:00:00', '2025-01-20 17:00:00'),
(4, 2, 'Material Check', 3, 'COMPLETED', '2025-01-20 17:00:00', '2025-01-21 10:00:00'),
(4, 3, 'Quality Inspection', 4, 'PENDING', '2025-01-21 10:00:00', NULL),

-- Tasks for instance 5 (MO24002 - Pending)
(5, 1, 'Production Plan Review', 2, 'PENDING', '2025-02-05 09:30:00', NULL);

-- ============================================
-- 5. Verify Data
-- ============================================
SELECT 'Users inserted:' AS info, COUNT(*) AS count FROM sys_user;
SELECT 'Workflow instances inserted:' AS info, COUNT(*) AS count FROM workflow_instance;
SELECT 'Workflow tasks inserted:' AS info, COUNT(*) AS count FROM workflow_task;

-- Show all users
SELECT user_id, username, real_name, email FROM sys_user;

-- Show all workflow instances
SELECT id, business_key, business_type, status, started_by FROM workflow_instance;

-- Show all workflow tasks
SELECT id, instance_id, task_name, assignee, status FROM workflow_task;

SELECT 'Data insertion completed!' AS result;
