-- 修复 budget_execution 表字段名
USE test;

-- 将带中文括号的字段名改为标准英文字段名
ALTER TABLE budget_execution 
CHANGE COLUMN `budget_amount(元)` budget_amount DECIMAL(10,2) COMMENT '预算金额 (元)',
CHANGE COLUMN `actual_amount(元)` actual_amount DECIMAL(10,2) COMMENT '实际金额 (元)',
CHANGE COLUMN `variance(元)` variance DECIMAL(10,2) COMMENT '差异 (元)',
CHANGE COLUMN `variance_rate(%)` variance_rate DECIMAL(5,2) COMMENT '差异率 (%)';

-- 优化 TEXT 类型字段
ALTER TABLE budget_execution 
MODIFY COLUMN period VARCHAR(20) COMMENT '期间 (yyyy-MM)',
MODIFY COLUMN budget_item VARCHAR(100) COMMENT '预算项目';

-- 验证修改后的表结构
DESCRIBE budget_execution;

-- 查看数据量
SELECT COUNT(*) AS total_records FROM budget_execution;

-- 查看示例数据
SELECT * FROM budget_execution LIMIT 5;
