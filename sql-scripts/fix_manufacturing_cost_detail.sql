-- 修复 manufacturing_cost_detail 表字段名
USE test;

-- 将 amount(元) 改为 amount
ALTER TABLE manufacturing_cost_detail CHANGE COLUMN `amount(元)` amount DECIMAL(10,2) COMMENT '金额 (元)';

-- 同时将其他 TEXT 类型字段改为更合适的类型
ALTER TABLE manufacturing_cost_detail 
MODIFY COLUMN cost_date DATE COMMENT '费用日期',
MODIFY COLUMN cost_category VARCHAR(100) COMMENT '费用类别',
MODIFY COLUMN production_line VARCHAR(100) COMMENT '生产线',
MODIFY COLUMN approver VARCHAR(100) COMMENT '审批人';

-- 验证修改后的表结构
DESCRIBE manufacturing_cost_detail;

-- 查看数据
SELECT * FROM manufacturing_cost_detail LIMIT 5;
