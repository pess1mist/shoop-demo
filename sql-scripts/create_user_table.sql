-- Create sys_user table
USE test;

-- Create user table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ENABLED',
    dept_code VARCHAR(50),
    dept_name VARCHAR(100),
    role VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Table';

-- Insert test users (matching the userId=1 in workflow tasks)
INSERT INTO sys_user (id, username, password, real_name, email, phone, status, dept_code, dept_name, role) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Admin User', 'admin@test.com', '13800138000', 'ENABLED', 'D001', 'IT Department', 'ADMIN'),
(2, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhang San', 'zhangsan@test.com', '13800138001', 'ENABLED', 'D002', 'Production Department', 'DEPT_MANAGER'),
(3, 'lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Li Si', 'lisi@test.com', '13800138002', 'ENABLED', 'D002', 'Production Department', 'STAFF'),
(4, 'wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wang Wu', 'wangwu@test.com', '13800138003', 'ENABLED', 'D003', 'Finance Department', 'FINANCE_MANAGER'),
(5, 'system', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'System', 'system@test.com', '13800138004', 'ENABLED', 'D000', 'System', 'SYSTEM'),
(6, 'sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Sun Qi', 'sunqi@test.com', '13800138005', 'ENABLED', 'D002', 'Production Department', 'DEPT_MANAGER'),
(7, 'zhouba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhou Ba', 'zhouba@test.com', '13800138006', 'ENABLED', 'D003', 'Finance Department', 'FINANCE_MANAGER'),
(8, 'wujiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wu Jiu', 'wujiu@test.com', '13800138007', 'ENABLED', 'D004', 'Management', 'GENERAL_MANAGER'),
(9, 'zhengshi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zheng Shi', 'zhengshi@test.com', '13800138008', 'ENABLED', 'D005', 'Alert Department', 'ALERT_MANAGER'),
(10, 'chenshiyi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Chen Shiyi', 'chenshiyi@test.com', '13800138009', 'ENABLED', 'D005', 'Alert Department', 'STAFF'),
(11, 'liushier', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Liu Shier', 'liushier@test.com', '13800138010', 'ENABLED', 'D005', 'Alert Department', 'STAFF');

-- Set auto_increment to avoid ID conflicts
ALTER TABLE sys_user AUTO_INCREMENT = 100;

-- Verify
SELECT 'User table created successfully!' AS result;
SELECT COUNT(*) AS user_count FROM sys_user;
SELECT id, username, real_name, dept_name, role FROM sys_user ORDER BY id;
