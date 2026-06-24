USE test;

-- 1. 工作流定义表
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

-- 2. 工作流节点表
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

-- 3. 工作流实例表
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

-- 4. 工作流任务表
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

-- 插入工作流定义数据
INSERT INTO workflow_definition (process_key, process_name, version, nodes, status) VALUES
('PURCHASE_PLAN', '采购计划审批流程', 1, 'SUBMIT,DEPT_APPROVE,FINANCE_APPROVE,END', 'ACTIVE'),
('BUDGET_ADJUST', '预算调整审批流程', 1, 'SUBMIT,FINANCE_APPROVE,GM_APPROVE,END', 'ACTIVE'),
('ALERT_WORKORDER', '预警工单处理流程', 1, 'CREATE,ASSIGN,PROCESS,RESOLVE,CLOSE', 'ACTIVE');

-- 插入采购计划审批流程节点
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(1, 'SUBMIT', '提交申请', 'START', NULL, 'DEPT_APPROVE', 1),
(1, 'DEPT_APPROVE', '部门经理审批', 'APPROVAL', 'DEPT_MANAGER', 'FINANCE_APPROVE', 2),
(1, 'FINANCE_APPROVE', '财务审批', 'APPROVAL', 'FINANCE_MANAGER', 'END', 3),
(1, 'END', '结束', 'END', NULL, NULL, 4);

-- 插入预算调整审批流程节点
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(2, 'SUBMIT', 'Submit Application', 'START', NULL, 'FINANCE_APPROVE', 1),
(2, 'FINANCE_APPROVE', 'Finance Approval', 'APPROVAL', 'FINANCE_MANAGER', 'GM_APPROVE', 2),
(2, 'GM_APPROVE', 'General Manager Approval', 'APPROVAL', 'GENERAL_MANAGER', 'END', 3),
(2, 'END', 'End', 'END', NULL, NULL, 4);

-- 插入预警工单处理流程节点
INSERT INTO workflow_node (definition_id, node_key, node_name, node_type, approver_role, next_node_key, sort_order) VALUES
(3, 'CREATE', 'Create Work Order', 'START', NULL, 'ASSIGN', 1),
(3, 'ASSIGN', 'Assign Handler', 'ASSIGN', 'ADMIN', 'PROCESS', 2),
(3, 'PROCESS', 'Process Work Order', 'PROCESS', 'HANDLER', 'RESOLVE', 3),
(3, 'RESOLVE', 'Resolve Confirmation', 'APPROVAL', 'CREATOR', 'CLOSE', 4),
(3, 'CLOSE', 'Close Work Order', 'END', NULL, NULL, 5);

-- 插入工作流实例数据（采购计划）
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time) VALUES
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401001', '原材料采购申请-钢材50吨', 1, '张三', 'COMPLETED', 'END', '2024-01-15 09:30:00', '2024-01-16 14:20:00'),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202401002', '设备配件采购申请', 2, '李四', 'RUNNING', 'FINANCE_APPROVE', '2024-01-18 10:15:00', NULL),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO202402001', '办公用品采购', 3, '王五', 'RUNNING', 'DEPT_APPROVE', '2024-02-05 11:00:00', NULL);

-- 插入工作流实例数据（预算调整）
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time) VALUES
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA202401001', 'Q1生产预算调整申请', 1, '张三', 'COMPLETED', 'END', '2024-01-20 14:00:00', '2024-01-22 16:30:00'),
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA202402001', '营销费用预算追加', 4, '赵六', 'RUNNING', 'GM_APPROVE', '2024-02-10 09:45:00', NULL);

-- 插入工作流实例数据（预警工单）
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, status, current_node_key, start_time) VALUES
(3, 'ALERT_WORKORDER', 'ALERT', 'AW202401001', '成本超预算预警处理', 5, '系统', 'COMPLETED', 'CLOSE', '2024-01-25 08:00:00', '2024-01-25 17:00:00'),
(3, 'ALERT_WORKORDER', 'ALERT', 'AW202402001', '价格异常预警', 5, '系统', 'RUNNING', 'PROCESS', '2024-02-15 10:30:00', NULL);

-- 插入工作任务数据（采购计划-已完成的）
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(1, 'SUBMIT', '提交申请', 1, '张三', 'COMPLETED', 'SUBMIT', '提交采购申请', '2024-01-15 09:30:00', '2024-01-15 09:30:00'),
(1, 'DEPT_APPROVE', '部门经理审批', 6, '孙七', 'COMPLETED', 'APPROVE', '同意，价格合理', '2024-01-15 14:00:00', '2024-01-15 15:30:00'),
(1, 'FINANCE_APPROVE', '财务审批', 7, '周八', 'COMPLETED', 'APPROVE', '预算充足，予以批准', '2024-01-16 10:00:00', '2024-01-16 14:20:00');

-- 插入工作任务数据（采购计划-进行中的）
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(2, 'SUBMIT', '提交申请', 2, '李四', 'COMPLETED', 'SUBMIT', '紧急采购需求', '2024-01-18 10:15:00', '2024-01-18 10:15:00'),
(2, 'DEPT_APPROVE', '部门经理审批', 6, '孙七', 'COMPLETED', 'APPROVE', '同意', '2024-01-18 15:00:00', '2024-01-18 16:00:00'),
(2, 'FINANCE_APPROVE', '财务审批', 7, '周八', 'PENDING', NULL, NULL, '2024-01-19 09:00:00', NULL);

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(3, 'SUBMIT', '提交申请', 3, '王五', 'COMPLETED', 'SUBMIT', '常规办公用品采购', '2024-02-05 11:00:00', '2024-02-05 11:00:00'),
(3, 'DEPT_APPROVE', '部门经理审批', 6, '孙七', 'PENDING', NULL, NULL, '2024-02-05 14:00:00', NULL);

-- 插入工作任务数据（预算调整）
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(4, 'SUBMIT', '提交申请', 1, '张三', 'COMPLETED', 'SUBMIT', '因市场需求变化调整预算', '2024-01-20 14:00:00', '2024-01-20 14:00:00'),
(4, 'FINANCE_APPROVE', '财务审批', 7, '周八', 'COMPLETED', 'APPROVE', '调整合理', '2024-01-21 10:00:00', '2024-01-21 11:30:00'),
(4, 'GM_APPROVE', '总经理审批', 8, '吴九', 'COMPLETED', 'APPROVE', '批准', '2024-01-22 15:00:00', '2024-01-22 16:30:00');

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(5, 'SUBMIT', '提交申请', 4, '赵六', 'COMPLETED', 'SUBMIT', '营销活动需要追加预算', '2024-02-10 09:45:00', '2024-02-10 09:45:00'),
(5, 'FINANCE_APPROVE', '财务审批', 7, '周八', 'COMPLETED', 'APPROVE', '同意追加', '2024-02-11 10:00:00', '2024-02-11 11:00:00'),
(5, 'GM_APPROVE', '总经理审批', 8, '吴九', 'PENDING', NULL, NULL, '2024-02-12 09:00:00', NULL);

-- 插入工作任务数据（预警工单）
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(6, 'CREATE', '创建工单', 5, '系统', 'COMPLETED', 'CREATE', '系统自动创建', '2024-01-25 08:00:00', '2024-01-25 08:00:00'),
(6, 'ASSIGN', '分配处理人', 9, '郑十', 'COMPLETED', 'ASSIGN', '分配给成本分析师', '2024-01-25 08:30:00', '2024-01-25 09:00:00'),
(6, 'PROCESS', '处理工单', 10, '陈十一', 'COMPLETED', 'PROCESS', '分析原因：原材料价格上涨', '2024-01-25 10:00:00', '2024-01-25 15:00:00'),
(6, 'RESOLVE', '解决确认', 5, '系统', 'COMPLETED', 'APPROVE', '已解决', '2024-01-25 16:00:00', '2024-01-25 16:30:00'),
(6, 'CLOSE', '关闭工单', 5, '系统', 'COMPLETED', 'CLOSE', '工单关闭', '2024-01-25 17:00:00', '2024-01-25 17:00:00');

INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, assignee_name, status, action, comment, start_time, end_time) VALUES
(7, 'CREATE', '创建工单', 5, '系统', 'COMPLETED', 'CREATE', '检测到价格异常', '2024-02-15 10:30:00', '2024-02-15 10:30:00'),
(7, 'ASSIGN', '分配处理人', 9, '郑十', 'COMPLETED', 'ASSIGN', '分配给采购专员', '2024-02-15 11:00:00', '2024-02-15 11:30:00'),
(7, 'PROCESS', '处理工单', 11, '刘十二', 'PENDING', NULL, NULL, '2024-02-15 14:00:00', NULL);
