CREATE DATABASE IF NOT EXISTS shuju DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shuju;

CREATE TABLE `product` (
    `product_code` VARCHAR(50) PRIMARY KEY,
    `product_name` VARCHAR(100) NOT NULL,
    `unit` VARCHAR(20) DEFAULT 'ton',
    `unit_price` DECIMAL(12,2) NOT NULL,
    `cost_price` DECIMAL(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `production_order` (
    `prod_order_id` VARCHAR(100) PRIMARY KEY,
    `product_code` VARCHAR(50) NOT NULL,
    `plan_quantity` INT NOT NULL,
    `actual_quantity` INT DEFAULT 0,
    `start_date` DATE NOT NULL,
    `end_date` DATE DEFAULT NULL,
    `material_cost` INT DEFAULT 0,
    `labor_cost` INT DEFAULT 0,
    `manu_cost` INT DEFAULT 0,
    `total_cost` INT DEFAULT 0,
    `status` VARCHAR(20) DEFAULT 'producing',
    KEY `idx_product_code` (`product_code`),
    KEY `idx_start_date` (`start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `supplier` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `supplier_code` VARCHAR(50) NOT NULL,
    `supplier_name` VARCHAR(200) NOT NULL,
    `contact` VARCHAR(50),
    `contact_phone` VARCHAR(20),
    `address` VARCHAR(500),
    `rating` VARCHAR(10) DEFAULT 'A',
    UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sales_order` (
    `order_id` VARCHAR(100) PRIMARY KEY,
    `order_date` DATE NOT NULL,
    `customer_id` VARCHAR(50) NOT NULL,
    `product_code` VARCHAR(50) NOT NULL,
    `product_name` VARCHAR(100) NOT NULL,
    `quantity` DECIMAL(12,2) NOT NULL,
    `unit_price` DECIMAL(12,2) NOT NULL,
    `total_amount` DECIMAL(14,2) NOT NULL,
    `approval_status` VARCHAR(20) DEFAULT 'approved',
    KEY `idx_order_date` (`order_date`),
    KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `purchase_order` (
    `order_id` VARCHAR(100) PRIMARY KEY,
    `order_date` DATE NOT NULL,
    `supplier_id` VARCHAR(50) NOT NULL,
    `material_code` VARCHAR(50) NOT NULL,
    `material_name` VARCHAR(100) NOT NULL,
    `quantity` DECIMAL(12,2) NOT NULL,
    `unit_price` DECIMAL(12,2) NOT NULL,
    `total_amount` DECIMAL(14,2) NOT NULL,
    `approval_status` VARCHAR(20) DEFAULT 'approved',
    KEY `idx_order_date` (`order_date`),
    KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `budget_master` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `year` INT NOT NULL,
    `period` VARCHAR(20) NOT NULL,
    `dept_code` VARCHAR(50),
    `dept_name` VARCHAR(100),
    `project_code` VARCHAR(50),
    `project_name` VARCHAR(100),
    `budget_type` VARCHAR(50),
    `budget_item` VARCHAR(100),
    `amount` DECIMAL(14,2),
    `status` VARCHAR(20) DEFAULT 'APPROVED',
    `created_by` BIGINT,
    `created_by_name` VARCHAR(50),
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `approved_by` BIGINT,
    `approved_by_name` VARCHAR(50),
    `approved_time` DATETIME,
    `remark` TEXT,
    KEY `idx_year_period` (`year`, `period`),
    KEY `idx_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `budget_execution` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `fiscal_year` INT NOT NULL,
    `period` VARCHAR(20) NOT NULL,
    `budget_item` VARCHAR(100) NOT NULL,
    `budget_amount` DECIMAL(14,2) NOT NULL,
    `actual_amount` DECIMAL(14,2) NOT NULL,
    `variance` DECIMAL(14,2),
    `variance_rate` DECIMAL(8,2),
    KEY `idx_fiscal_year_period` (`fiscal_year`, `period`),
    KEY `idx_budget_item` (`budget_item`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `purchase_price_history` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `material_code` VARCHAR(50) NOT NULL,
    `order_date` DATE NOT NULL,
    `supplier_id` VARCHAR(50),
    `unit_price` DECIMAL(12,2) NOT NULL,
    `remark` TEXT,
    KEY `idx_material_code` (`material_code`),
    KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `purchase_plan` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `plan_code` VARCHAR(50) NOT NULL,
    `plan_name` VARCHAR(200),
    `dept_code` VARCHAR(50),
    `dept_name` VARCHAR(100),
    `supplier_code` VARCHAR(50),
    `supplier_name` VARCHAR(200),
    `material_code` VARCHAR(50),
    `material_name` VARCHAR(100),
    `quantity` INT,
    `unit_price` DECIMAL(12,2),
    `total_amount` DECIMAL(14,2),
    `budget_id` BIGINT,
    `budget_item` VARCHAR(100),
    `plan_date` DATE,
    `require_date` DATE,
    `status` VARCHAR(20) DEFAULT 'DRAFT',
    `workflow_instance_id` BIGINT,
    `created_by` BIGINT,
    `created_by_name` VARCHAR(50),
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `remark` TEXT,
    UNIQUE KEY `uk_plan_code` (`plan_code`),
    KEY `idx_dept_code` (`dept_code`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `purchase_approval` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `approval_no` VARCHAR(50) NOT NULL,
    `purchase_plan_id` BIGINT,
    `approver_id` BIGINT,
    `approver_name` VARCHAR(50),
    `approval_status` VARCHAR(20) DEFAULT 'PENDING',
    `approval_time` DATETIME,
    `approval_comment` TEXT,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_approval_no` (`approval_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `manufacturing_cost_detail` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `cost_date` DATE NOT NULL,
    `cost_category` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `production_line` VARCHAR(50),
    `approver` VARCHAR(50),
    KEY `idx_cost_date` (`cost_date`),
    KEY `idx_cost_category` (`cost_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `material_price_fluctuation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `material_code` VARCHAR(50),
    `material_name` VARCHAR(100),
    `supplier_id` BIGINT,
    `supplier_name` VARCHAR(200),
    `purchase_date` DATE,
    `unit_price` DECIMAL(12,2),
    `quantity` DECIMAL(12,2),
    `total_amount` DECIMAL(14,2),
    `price_change_rate` DECIMAL(8,4),
    `budget_amount` DECIMAL(14,2),
    `budget_variance` DECIMAL(14,2),
    `accounting_period` VARCHAR(20),
    `remark` TEXT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_material_code` (`material_code`),
    KEY `idx_purchase_date` (`purchase_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `business_data` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `business_code` VARCHAR(50),
    `business_type` VARCHAR(50),
    `department_code` VARCHAR(50),
    `project_name` VARCHAR(100),
    `amount` DECIMAL(14,2),
    `business_date` DATETIME,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `source_system` VARCHAR(50),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT DEFAULT 0,
    KEY `idx_business_code` (`business_code`),
    KEY `idx_department_code` (`department_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `financial_data` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `voucher_code` VARCHAR(50),
    `subject_code` VARCHAR(50),
    `subject_name` VARCHAR(100),
    `debit_amount` DECIMAL(14,2),
    `credit_amount` DECIMAL(14,2),
    `balance` DECIMAL(14,2),
    `voucher_date` DATETIME,
    `business_code` VARCHAR(50),
    `business_data_id` BIGINT,
    `accounting_period` VARCHAR(20),
    `cost_center` VARCHAR(50),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT DEFAULT 0,
    KEY `idx_voucher_code` (`voucher_code`),
    KEY `idx_subject_code` (`subject_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `budget_control` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `budget_code` VARCHAR(50),
    `budget_type` VARCHAR(50),
    `department_code` VARCHAR(50),
    `project_name` VARCHAR(100),
    `budget_amount` DECIMAL(14,2),
    `used_amount` DECIMAL(14,2),
    `remaining_amount` DECIMAL(14,2),
    `control_rate` DECIMAL(8,2),
    `start_date` DATETIME,
    `end_date` DATETIME,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT DEFAULT 0,
    KEY `idx_budget_code` (`budget_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `trend_prediction` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `prediction_type` VARCHAR(50),
    `department_code` VARCHAR(50),
    `project_name` VARCHAR(100),
    `predicted_value` DECIMAL(14,2),
    `actual_value` DECIMAL(14,2),
    `prediction_time` DATETIME,
    `target_time` DATETIME,
    `confidence_level` VARCHAR(20),
    `algorithm` VARCHAR(50),
    `analysis_result` TEXT,
    `suggestion` TEXT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_prediction_type` (`prediction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `monitor_alert` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `alert_code` VARCHAR(50),
    `alert_type` VARCHAR(50),
    `alert_level` VARCHAR(20),
    `alert_title` VARCHAR(200),
    `alert_content` TEXT,
    `threshold_value` DECIMAL(14,2),
    `actual_value` DECIMAL(14,2),
    `alert_time` DATETIME,
    `status` VARCHAR(20) DEFAULT 'NEW',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_alert_type` (`alert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `internal_control_log` (
    `log_id` VARCHAR(100) PRIMARY KEY,
    `alert_type` VARCHAR(50),
    `related_doc_no` VARCHAR(100),
    `alert_time` DATETIME,
    `alert_content` TEXT,
    `handle_status` VARCHAR(20) DEFAULT 'pending',
    `handler` VARCHAR(50),
    KEY `idx_alert_type` (`alert_type`),
    KEY `idx_handle_status` (`handle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `alert_work_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `alert_id` BIGINT,
    `alert_type` VARCHAR(50),
    `title` VARCHAR(200),
    `description` TEXT,
    `priority` VARCHAR(20) DEFAULT 'MEDIUM',
    `assignee_id` BIGINT,
    `assignee_name` VARCHAR(50),
    `status` VARCHAR(20) DEFAULT 'NEW',
    `solution` TEXT,
    `resolved_time` DATETIME,
    `closed_by` BIGINT,
    `closed_time` DATETIME,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `remark` TEXT,
    KEY `idx_alert_type` (`alert_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `budget_adjust_apply` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `budget_id` BIGINT,
    `adjust_type` VARCHAR(20),
    `adjust_amount` DECIMAL(14,2),
    `adjust_reason` TEXT,
    `before_amount` DECIMAL(14,2),
    `after_amount` DECIMAL(14,2),
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `applicant_id` BIGINT,
    `applicant_name` VARCHAR(50),
    `apply_time` DATETIME,
    `approver_id` BIGINT,
    `approver_name` VARCHAR(50),
    `approve_time` DATETIME,
    `approve_remark` TEXT,
    KEY `idx_budget_id` (`budget_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_bom` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `product_code` VARCHAR(50) NOT NULL,
    `product_name` VARCHAR(100),
    `material_code` VARCHAR(50),
    `material_name` VARCHAR(100),
    `usage_amount` DECIMAL(12,2),
    `unit` VARCHAR(20),
    `loss_rate` DECIMAL(8,2),
    `version` VARCHAR(20) DEFAULT 'V1.0',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_product_code` (`product_code`),
    KEY `idx_material_code` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `real_name` VARCHAR(50),
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `department` VARCHAR(100),
    `role` VARCHAR(50) DEFAULT 'USER',
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `workflow_definition` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `process_key` VARCHAR(100) NOT NULL,
    `process_name` VARCHAR(200) NOT NULL,
    `version` INT DEFAULT 1,
    `nodes` TEXT,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_process_key_version` (`process_key`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `workflow_node` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `definition_id` BIGINT NOT NULL,
    `node_key` VARCHAR(100) NOT NULL,
    `node_name` VARCHAR(200) NOT NULL,
    `node_type` VARCHAR(20),
    `approver_role` VARCHAR(50),
    `next_node_key` VARCHAR(100),
    `sort_order` INT DEFAULT 0,
    KEY `idx_definition_id` (`definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `workflow_instance` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `process_id` BIGINT NOT NULL,
    `process_key` VARCHAR(100) NOT NULL,
    `business_key` VARCHAR(100),
    `business_type` VARCHAR(50),
    `title` VARCHAR(200),
    `initiator_id` BIGINT,
    `initiator_name` VARCHAR(50),
    `current_node_key` VARCHAR(100),
    `current_node_id` BIGINT,
    `status` VARCHAR(20) DEFAULT 'RUNNING',
    `started_by` BIGINT,
    `started_time` DATETIME,
    `ended_time` DATETIME,
    `start_time` DATETIME,
    `end_time` DATETIME,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_process_key` (`process_key`),
    KEY `idx_status` (`status`),
    KEY `idx_initiator_id` (`initiator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `workflow_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `instance_id` BIGINT NOT NULL,
    `node_key` VARCHAR(100) NOT NULL,
    `node_name` VARCHAR(200) NOT NULL,
    `assignee_id` BIGINT,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `action` VARCHAR(20),
    `comment` TEXT,
    `start_time` DATETIME,
    `end_time` DATETIME,
    KEY `idx_instance_id` (`instance_id`),
    KEY `idx_assignee_id` (`assignee_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert user data
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `department`, `role`, `status`) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'Admin', 'Management', 'ADMIN', 'ACTIVE'),
(2, 'manager1', 'e10adc3949ba59abbe56e057f20f883e', 'Manager Zhang', 'Production', 'DEPT_MANAGER', 'ACTIVE'),
(3, 'finance1', 'e10adc3949ba59abbe56e057f20f883e', 'Finance Li', 'Finance', 'FINANCE_MANAGER', 'ACTIVE'),
(4, 'buyer1', 'e10adc3949ba59abbe56e057f20f883e', 'Buyer Wang', 'Purchase', 'BUYER', 'ACTIVE'),
(5, 'sales1', 'e10adc3949ba59abbe56e057f20f883e', 'Sales Zhao', 'Sales', 'SALES', 'ACTIVE');

-- Insert product data
INSERT INTO `product` (`product_code`, `product_name`, `unit`, `unit_price`, `cost_price`) VALUES
('P001', 'PET Film', 'ton', 12500, 9800),
('P002', 'BOPP Film', 'ton', 11800, 9200),
('P003', 'CPP Film', 'ton', 10500, 8100),
('P004', 'PE Film', 'ton', 9800, 7500);

-- Insert supplier data
INSERT INTO `supplier` (`id`, `supplier_code`, `supplier_name`, `contact`, `contact_phone`, `address`, `rating`) VALUES
(1, 'S001', 'Huarun Petrochemical', 'Mr. Zhang', '13800138001', 'Beijing', 'A'),
(2, 'S002', 'Sinopec', 'Mr. Li', '13800138002', 'Shanghai', 'A'),
(3, 'S003', 'Baofeng Energy', 'Mr. Wang', '13800138003', 'Ningxia', 'B'),
(4, 'S004', 'Wanhua Chemical', 'Mr. Zhao', '13800138004', 'Shandong', 'A');

-- Insert sales order data
INSERT INTO `sales_order` (`order_id`, `order_date`, `customer_id`, `product_code`, `product_name`, `quantity`, `unit_price`, `total_amount`, `approval_status`) VALUES
('SO2025001', '2025-01-15', 'C001', 'P001', 'PET Film', 50, 12500, 625000, 'approved'),
('SO2025002', '2025-02-20', 'C002', 'P002', 'BOPP Film', 80, 11800, 944000, 'approved'),
('SO2025003', '2025-03-10', 'C001', 'P003', 'CPP Film', 60, 10500, 630000, 'approved'),
('SO2025004', '2025-04-05', 'C003', 'P001', 'PET Film', 45, 12500, 562500, 'approved'),
('SO2025005', '2025-05-12', 'C002', 'P004', 'PE Film', 70, 9800, 686000, 'approved'),
('SO2025006', '2025-06-18', 'C004', 'P002', 'BOPP Film', 55, 11800, 649000, 'approved'),
('SO2025007', '2025-07-22', 'C001', 'P003', 'CPP Film', 90, 10500, 945000, 'approved'),
('SO2025008', '2025-08-30', 'C003', 'P001', 'PET Film', 40, 12500, 500000, 'approved'),
('SO2025009', '2025-09-15', 'C002', 'P004', 'PE Film', 65, 9800, 637000, 'approved'),
('SO2025010', '2025-10-20', 'C004', 'P002', 'BOPP Film', 75, 11800, 885000, 'approved');

-- Insert purchase order data
INSERT INTO `purchase_order` (`order_id`, `order_date`, `supplier_id`, `material_code`, `material_name`, `quantity`, `unit_price`, `total_amount`, `approval_status`) VALUES
('PO2025001', '2025-01-10', '1', 'M001', 'PET Chips', 60, 8500, 510000, 'approved'),
('PO2025002', '2025-02-15', '2', 'M002', 'PP Resin', 90, 7800, 702000, 'approved'),
('PO2025003', '2025-03-20', '3', 'M003', 'PE Resin', 70, 6500, 455000, 'approved'),
('PO2025004', '2025-04-25', '1', 'M001', 'PET Chips', 55, 8600, 473000, 'approved'),
('PO2025005', '2025-05-18', '4', 'M004', 'Additives', 30, 12000, 360000, 'approved'),
('PO2025006', '2025-06-22', '2', 'M002', 'PP Resin', 85, 7900, 671500, 'approved'),
('PO2025007', '2025-07-15', '3', 'M003', 'PE Resin', 75, 6600, 495000, 'approved'),
('PO2025008', '2025-08-10', '1', 'M001', 'PET Chips', 50, 8700, 435000, 'approved'),
('PO2025009', '2025-09-20', '4', 'M004', 'Additives', 25, 12500, 312500, 'approved'),
('PO2025010', '2025-10-15', '2', 'M002', 'PP Resin', 80, 8000, 640000, 'approved');

-- Insert production order data
INSERT INTO `production_order` (`prod_order_id`, `product_code`, `plan_quantity`, `actual_quantity`, `start_date`, `end_date`, `material_cost`, `labor_cost`, `manu_cost`, `total_cost`, `status`) VALUES
('PO-2025001', 'P001', 50, 48, '2025-01-05', '2025-01-25', 250000, 80000, 45000, 375000, 'completed'),
('PO-2025002', 'P002', 80, 78, '2025-02-10', '2025-02-28', 350000, 120000, 65000, 535000, 'completed'),
('PO-2025003', 'P003', 60, 55, '2025-03-05', '2025-03-25', 280000, 95000, 52000, 427000, 'completed'),
('PO-2025004', 'P001', 45, 45, '2025-04-01', '2025-04-20', 230000, 75000, 42000, 347000, 'producing'),
('PO-2025005', 'P004', 70, 30, '2025-05-05', '2025-05-25', 300000, 105000, 58000, 463000, 'producing'),
('PO-2025006', 'P002', 55, 0, '2025-06-10', '2025-06-30', 0, 0, 0, 0, 'producing'),
('PO-2025007', 'P003', 90, 0, '2025-07-15', '2025-08-05', 0, 0, 0, 0, 'producing');

-- Insert budget execution data
INSERT INTO `budget_execution` (`fiscal_year`, `period`, `budget_item`, `budget_amount`, `actual_amount`, `variance`, `variance_rate`) VALUES
(2023, '2023-01', 'material', 5000000, 4850000, 150000, 3.00),
(2023, '2023-02', 'material', 5000000, 5120000, -120000, -2.40),
(2023, '2023-03', 'material', 5000000, 4980000, 20000, 0.40),
(2023, '2023-04', 'material', 5000000, 5200000, -200000, -4.00),
(2023, '2023-05', 'material', 5000000, 4900000, 100000, 2.00),
(2023, '2023-06', 'material', 5000000, 5050000, -50000, -1.00),
(2023, '2023-01', 'labor', 2000000, 1950000, 50000, 2.50),
(2023, '2023-02', 'labor', 2000000, 2020000, -20000, -1.00),
(2023, '2023-03', 'labor', 2000000, 1980000, 20000, 1.00),
(2023, '2023-04', 'labor', 2000000, 2050000, -50000, -2.50),
(2023, '2023-05', 'labor', 2000000, 1970000, 30000, 1.50),
(2023, '2023-06', 'labor', 2000000, 2010000, -10000, -0.50),
(2023, '2023-01', 'manufacturing', 1500000, 1480000, 20000, 1.33),
(2023, '2023-02', 'manufacturing', 1500000, 1520000, -20000, -1.33),
(2023, '2023-03', 'manufacturing', 1500000, 1490000, 10000, 0.67),
(2023, '2023-04', 'manufacturing', 1500000, 1540000, -40000, -2.67),
(2023, '2023-05', 'manufacturing', 1500000, 1470000, 30000, 2.00),
(2023, '2023-06', 'manufacturing', 1500000, 1510000, -10000, -0.67),
(2024, '2024-01', 'material', 5200000, 5050000, 150000, 2.88),
(2024, '2024-02', 'material', 5200000, 5180000, 20000, 0.38),
(2024, '2024-03', 'material', 5200000, 5300000, -100000, -1.92),
(2024, '2024-04', 'material', 5200000, 5150000, 50000, 0.96),
(2024, '2024-05', 'material', 5200000, 5220000, -20000, -0.38),
(2024, '2024-06', 'material', 5200000, 5100000, 100000, 1.92),
(2024, '2024-01', 'labor', 2100000, 2080000, 20000, 0.95),
(2024, '2024-02', 'labor', 2100000, 2120000, -20000, -0.95),
(2024, '2024-03', 'labor', 2100000, 2090000, 10000, 0.48),
(2024, '2024-04', 'labor', 2100000, 2130000, -30000, -1.43),
(2024, '2024-05', 'labor', 2100000, 2070000, 30000, 1.43),
(2024, '2024-06', 'labor', 2100000, 2110000, -10000, -0.48),
(2024, '2024-01', 'manufacturing', 1550000, 1530000, 20000, 1.29),
(2024, '2024-02', 'manufacturing', 1550000, 1560000, -10000, -0.65),
(2024, '2024-03', 'manufacturing', 1550000, 1540000, 10000, 0.65),
(2024, '2024-04', 'manufacturing', 1550000, 1580000, -30000, -1.94),
(2024, '2024-05', 'manufacturing', 1550000, 1520000, 30000, 1.94),
(2024, '2024-06', 'manufacturing', 1550000, 1560000, -10000, -0.65);

-- Insert manufacturing cost detail data
INSERT INTO `manufacturing_cost_detail` (`cost_date`, `cost_category`, `amount`, `production_line`, `approver`) VALUES
('2025-01-10', 'electricity', 85000, 'extrusion', 'Manager Zhang'),
('2025-01-10', 'electricity', 72000, 'emulsion', 'Manager Zhang'),
('2025-01-10', 'depreciation', 45000, 'extrusion', 'Manager Zhang'),
('2025-01-10', 'depreciation', 38000, 'emulsion', 'Manager Zhang'),
('2025-01-10', 'maintenance', 12000, 'extrusion', 'Manager Zhang'),
('2025-01-10', 'maintenance', 8500, 'emulsion', 'Manager Zhang'),
('2025-02-10', 'electricity', 88000, 'extrusion', 'Manager Zhang'),
('2025-02-10', 'electricity', 75000, 'emulsion', 'Manager Zhang'),
('2025-02-10', 'depreciation', 45000, 'extrusion', 'Manager Zhang'),
('2025-02-10', 'depreciation', 38000, 'emulsion', 'Manager Zhang'),
('2025-02-10', 'maintenance', 15000, 'extrusion', 'Manager Zhang'),
('2025-02-10', 'maintenance', 9500, 'emulsion', 'Manager Zhang'),
('2025-03-10', 'electricity', 92000, 'extrusion', 'Manager Zhang'),
('2025-03-10', 'electricity', 78000, 'emulsion', 'Manager Zhang'),
('2025-03-10', 'depreciation', 45000, 'extrusion', 'Manager Zhang'),
('2025-03-10', 'depreciation', 38000, 'emulsion', 'Manager Zhang'),
('2025-03-10', 'maintenance', 18000, 'extrusion', 'Manager Zhang'),
('2025-03-10', 'maintenance', 11000, 'emulsion', 'Manager Zhang');

-- Insert budget master data
INSERT INTO `budget_master` (`id`, `year`, `period`, `dept_code`, `dept_name`, `budget_type`, `budget_item`, `amount`, `status`, `created_by`, `created_by_name`, `created_time`, `approved_by`, `approved_by_name`, `approved_time`) VALUES
(1, 2025, 'Q1', 'PROD', 'Production Dept', 'cost', 'material', 5200000, 'APPROVED', 2, 'Manager Zhang', '2024-12-01 10:00:00', 3, 'Finance Li', '2024-12-15 14:00:00'),
(2, 2025, 'Q1', 'PROD', 'Production Dept', 'cost', 'labor', 2100000, 'APPROVED', 2, 'Manager Zhang', '2024-12-01 10:00:00', 3, 'Finance Li', '2024-12-15 14:00:00'),
(3, 2025, 'Q1', 'PROD', 'Production Dept', 'cost', 'manufacturing', 1550000, 'APPROVED', 2, 'Manager Zhang', '2024-12-01 10:00:00', 3, 'Finance Li', '2024-12-15 14:00:00'),
(4, 2025, 'Q2', 'PROD', 'Production Dept', 'cost', 'material', 5300000, 'APPROVED', 2, 'Manager Zhang', '2025-03-01 10:00:00', 3, 'Finance Li', '2025-03-15 14:00:00'),
(5, 2025, 'Q2', 'PROD', 'Production Dept', 'cost', 'labor', 2150000, 'APPROVED', 2, 'Manager Zhang', '2025-03-01 10:00:00', 3, 'Finance Li', '2025-03-15 14:00:00'),
(6, 2025, 'Q2', 'PROD', 'Production Dept', 'cost', 'manufacturing', 1580000, 'APPROVED', 2, 'Manager Zhang', '2025-03-01 10:00:00', 3, 'Finance Li', '2025-03-15 14:00:00'),
(7, 2025, 'Q1', 'SALE', 'Sales Dept', 'expense', 'sales', 800000, 'APPROVED', 5, 'Sales Zhao', '2024-12-01 10:00:00', 3, 'Finance Li', '2024-12-15 14:00:00'),
(8, 2025, 'Q1', 'PURC', 'Purchase Dept', 'expense', 'purchase', 500000, 'APPROVED', 4, 'Buyer Wang', '2024-12-01 10:00:00', 3, 'Finance Li', '2024-12-15 14:00:00');

-- Insert workflow definition data
INSERT INTO `workflow_definition` (`id`, `process_key`, `process_name`, `version`, `nodes`, `status`) VALUES
(1, 'PURCHASE_PLAN', 'Purchase Plan Approval', 1, '[{"nodeKey":"start","nodeName":"Start","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"Dept Manager Approval","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"Finance Manager Approval","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"End","nodeType":"END"}]', 'ACTIVE'),
(2, 'BUDGET_ADJUST', 'Budget Adjustment Approval', 1, '[{"nodeKey":"start","nodeName":"Start","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"Dept Manager Approval","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"Finance Manager Approval","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"End","nodeType":"END"}]', 'ACTIVE'),
(3, 'SALES_ORDER', 'Sales Order Approval', 1, '[{"nodeKey":"start","nodeName":"Start","nodeType":"START"},{"nodeKey":"sales_approve","nodeName":"Sales Manager Approval","nodeType":"APPROVAL","approverRole":"SALES"},{"nodeKey":"end","nodeName":"End","nodeType":"END"}]', 'ACTIVE');

-- Insert workflow node data
INSERT INTO `workflow_node` (`definition_id`, `node_key`, `node_name`, `node_type`, `approver_role`, `next_node_key`, `sort_order`) VALUES
(1, 'start', 'Start', 'START', NULL, 'dept_approve', 1),
(1, 'dept_approve', 'Dept Manager Approval', 'APPROVAL', 'DEPT_MANAGER', 'finance_approve', 2),
(1, 'finance_approve', 'Finance Manager Approval', 'APPROVAL', 'FINANCE_MANAGER', 'end', 3),
(1, 'end', 'End', 'END', NULL, NULL, 4),
(2, 'start', 'Start', 'START', NULL, 'dept_approve', 1),
(2, 'dept_approve', 'Dept Manager Approval', 'APPROVAL', 'DEPT_MANAGER', 'finance_approve', 2),
(2, 'finance_approve', 'Finance Manager Approval', 'APPROVAL', 'FINANCE_MANAGER', 'end', 3),
(2, 'end', 'End', 'END', NULL, NULL, 4),
(3, 'start', 'Start', 'START', NULL, 'sales_approve', 1),
(3, 'sales_approve', 'Sales Manager Approval', 'APPROVAL', 'SALES', 'end', 2),
(3, 'end', 'End', 'END', NULL, NULL, 3);
