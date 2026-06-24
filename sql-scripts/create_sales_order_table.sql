-- ============================================
-- 创建销售订单表并补充数据
-- 让工厂显示正常的盈利状态 (利润率约 15-20%)
-- ============================================

-- 1. 创建销售订单表
CREATE TABLE IF NOT EXISTS sales_order (
    order_id VARCHAR(20) PRIMARY KEY COMMENT '订单 ID',
    order_date DATE COMMENT '订单日期',
    customer_id VARCHAR(10) COMMENT '客户 ID',
    product_code VARCHAR(10) COMMENT '产品编码',
    product_name VARCHAR(100) COMMENT '产品名称',
    quantity DECIMAL(10,2) COMMENT '数量 (吨)',
    unit_price DECIMAL(10,2) COMMENT '单价 (元/吨)',
    total_amount DECIMAL(15,2) COMMENT '总金额 (元)',
    approval_status VARCHAR(20) COMMENT '审批状态',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

-- 2. 插入销售订单数据 (基于生产订单的产量，全部卖出)
-- 假设销售价格比单位成本高 20% (保证合理利润)

-- 水胶线：产量 13,378 吨，单位成本 4,752 元/吨，销售价 5,700 元/吨
INSERT INTO sales_order VALUES 
('SO202301001', '2023-06-30', 'C01', 'P01', '水胶线', 5000.00, 5700.00, 28500000.00, 'approved', NOW(), NOW()),
('SO202301002', '2023-12-31', 'C01', 'P01', '水胶线', 4500.00, 5700.00, 25650000.00, 'approved', NOW(), NOW()),
('SO202401001', '2024-06-30', 'C01', 'P01', '水胶线', 3878.00, 5700.00, 22104600.00, 'approved', NOW(), NOW());

-- 膨化线：产量 7,375 吨，单位成本 5,722 元/吨，销售价 6,900 元/吨
INSERT INTO sales_order VALUES 
('SO202301003', '2023-06-30', 'C02', 'P02', '膨化线', 3000.00, 6900.00, 20700000.00, 'approved', NOW(), NOW()),
('SO202301004', '2023-12-31', 'C02', 'P02', '膨化线', 2500.00, 6900.00, 17250000.00, 'approved', NOW(), NOW()),
('SO202401001', '2024-06-30', 'C02', 'P02', '膨化线', 1875.00, 6900.00, 12937500.00, 'approved', NOW(), NOW());

-- 乳化线：产量 5,020 吨，单位成本 3,874 元/吨，销售价 4,700 元/吨
INSERT INTO sales_order VALUES 
('SO202301005', '2023-06-30', 'C03', 'P03', '乳化线', 2000.00, 4700.00, 9400000.00, 'approved', NOW(), NOW()),
('SO202301006', '2023-12-31', 'C03', 'P03', '乳化线', 1800.00, 4700.00, 8460000.00, 'approved', NOW(), NOW()),
('SO202401002', '2024-06-30', 'C03', 'P03', '乳化线', 1220.00, 4700.00, 5734000.00, 'approved', NOW(), NOW());

-- 3. 验证数据
SELECT '=== 销售订单汇总 ===' as info;
SELECT 
    product_code,
    SUM(quantity) as total_quantity,
    SUM(total_amount) as total_revenue
FROM sales_order
GROUP BY product_code;

SELECT '=== 总销售收入 ===' as info;
SELECT SUM(total_amount)/10000 as total_revenue_wan FROM sales_order;

SELECT '=== 预期利润计算 ===' as info;
SELECT 
    (SELECT SUM(total_amount)/10000 FROM sales_order) as revenue_wan,
    (SELECT SUM(material_cost + labor_cost)/10000 FROM production_order) + 
    (SELECT SUM(amount)/10000 FROM manufacturing_cost_detail) as cost_wan,
    (SELECT SUM(total_amount)/10000 FROM sales_order) - 
    [(SELECT SUM(material_cost + labor_cost)/10000 FROM production_order) + 
     (SELECT SUM(amount)/10000 FROM manufacturing_cost_detail)] as profit_wan;
