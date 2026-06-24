-- H2 数据库初始化脚本
-- 用于开发和测试环境

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(200) NOT NULL,
    category VARCHAR(50),
    unit_price DECIMAL(10,2),
    cost_price DECIMAL(10,2),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 生产订单表
CREATE TABLE IF NOT EXISTS production_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    product_id BIGINT,
    quantity INT,
    status VARCHAR(20),
    start_date DATE,
    end_date DATE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 销售订单表
CREATE TABLE IF NOT EXISTS sales_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100),
    amount DECIMAL(10,2),
    status VARCHAR(20),
    order_date DATE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 预算执行表
CREATE TABLE IF NOT EXISTS budget_execution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    budget_amount DECIMAL(10,2),
    actual_amount DECIMAL(10,2),
    execution_rate DECIMAL(5,2),
    budget_year INT,
    budget_month INT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 流程定义表
CREATE TABLE IF NOT EXISTS workflow_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 流程实例表
CREATE TABLE IF NOT EXISTS workflow_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_definition_id BIGINT,
    status VARCHAR(20),
    current_node VARCHAR(50),
    initiator_id BIGINT,
    initiator_name VARCHAR(50),
    business_key VARCHAR(100),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 流程任务表
CREATE TABLE IF NOT EXISTS workflow_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instance_id BIGINT,
    node_id BIGINT,
    task_name VARCHAR(100),
    assignee BIGINT,
    status VARCHAR(20),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_time TIMESTAMP
);

-- 插入测试数据
-- 默认管理员用户（密码：123456，使用 BCrypt 加密）
INSERT INTO sys_user (username, password, real_name, email, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '管理员', 'admin@example.com', 'ACTIVE'),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '张三', 'zhangsan@example.com', 'ACTIVE'),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '李四', 'lisi@example.com', 'ACTIVE');

-- 插入测试产品
INSERT INTO product (product_code, product_name, category, unit_price, cost_price) VALUES
('P001', '产品 A', '电子', 100.00, 60.00),
('P002', '产品 B', '机械', 200.00, 120.00),
('P003', '产品 C', '化工', 150.00, 90.00);

-- 插入测试流程定义
INSERT INTO workflow_definition (name, code, status) VALUES
('采购审批流程', 'PURCHASE_APPROVAL', 'ACTIVE'),
('生产计划流程', 'PRODUCTION_PLAN', 'ACTIVE'),
('费用报销流程', 'EXPENSE_REIMBURSEMENT', 'ACTIVE');
