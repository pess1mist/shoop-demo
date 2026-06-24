-- 创建缺失的核心表到 test 数据库
USE test;

-- 1. 创建 product 表
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` varchar(20) NOT NULL,
  `product_name` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建 purchase_order 表
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `order_id` varchar(20) NOT NULL,
  `order_date` date DEFAULT NULL,
  `supplier_id` varchar(10) DEFAULT NULL,
  `material_code` varchar(20) DEFAULT NULL,
  `material_name` varchar(100) DEFAULT NULL,
  `quantity` decimal(10,2) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `total_amount` decimal(15,2) DEFAULT NULL,
  `approval_status` varchar(20) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建 sys_user 表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` int DEFAULT 1,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建 workflow_instance 表
CREATE TABLE IF NOT EXISTS `workflow_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `process_id` bigint DEFAULT NULL,
  `business_key` varchar(50) DEFAULT NULL,
  `business_type` varchar(50) DEFAULT NULL,
  `current_node_id` bigint DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `started_by` bigint DEFAULT NULL,
  `started_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `ended_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 创建 workflow_task 表
CREATE TABLE IF NOT EXISTS `workflow_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `instance_id` bigint DEFAULT NULL,
  `node_id` bigint DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `assignee` bigint DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `completed_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert test data
INSERT INTO product VALUES ('P01', 'Product A', 'Type 1', 3500.00, 2800.00);
INSERT INTO product VALUES ('P02', 'Product B', 'Type 2', 4200.00, 3500.00);
INSERT INTO product VALUES ('P03', 'Product C', 'Type 3', 3800.00, 3100.00);

-- Insert admin user
INSERT INTO sys_user (username, password, real_name, email, phone, status) 
VALUES ('admin', '123456', 'Admin', 'admin@test.com', '13800138000', 1);

SELECT 'Tables created!' AS result;
SHOW TABLES;
