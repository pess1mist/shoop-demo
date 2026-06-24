-- 修复 production_order 表字段名
USE test;

-- 将带中文括号的字段名改为标准英文字段名
ALTER TABLE production_order 
CHANGE COLUMN `plan_quantity(吨)` plan_quantity INT COMMENT '计划产量 (吨)',
CHANGE COLUMN `actual_quantity(吨)` actual_quantity INT COMMENT '实际产量 (吨)',
CHANGE COLUMN `material_cost(元)` material_cost INT COMMENT '材料成本 (元)',
CHANGE COLUMN `labor_cost(元)` labor_cost INT COMMENT '人工成本 (元)',
CHANGE COLUMN `manu_cost(元)` manu_cost INT COMMENT '制造费用 (元)',
CHANGE COLUMN `total_cost(元)` total_cost INT COMMENT '总成本 (元)';

-- 优化 TEXT 类型字段
ALTER TABLE production_order 
MODIFY COLUMN prod_order_id VARCHAR(100) COMMENT '生产订单 ID',
MODIFY COLUMN product_code VARCHAR(100) COMMENT '产品编码',
MODIFY COLUMN start_date DATE COMMENT '开始日期',
MODIFY COLUMN end_date DATE COMMENT '结束日期';

-- 添加订单状态字段 (如果不存在)
ALTER TABLE production_order 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) COMMENT '订单状态：生产中/已完成/暂停/异常';

-- 验证修改后的表结构
DESCRIBE production_order;

-- 查看数据量
SELECT COUNT(*) AS total_records FROM production_order;

-- 查看示例数据
SELECT * FROM production_order LIMIT 5;
