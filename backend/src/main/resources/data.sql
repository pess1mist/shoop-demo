-- 插入用户数据
INSERT INTO sys_user (username, password, real_name, email, phone, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin@example.com', '13800138000', 'ENABLED'),
('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@example.com', '13800138001', 'ENABLED'),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@example.com', '13800138002', 'ENABLED');

-- 插入产品数据
INSERT INTO product (product_code, product_name, category, specification, unit_price, cost_price, stock_quantity, status) VALUES
('P001', '乳胶漆', '建筑材料', '白色 20L', 280.00, 200.00, 100, 'ACTIVE'),
('P002', '瓷砖', '建筑材料', '800x800mm', 120.00, 80.00, 200, 'ACTIVE'),
('P003', '水泥', '建筑材料', '42.5级 50kg', 45.00, 30.00, 500, 'ACTIVE'),
('P004', '钢筋', '建筑材料', 'Φ12mm', 5000.00, 4500.00, 100, 'ACTIVE'),
('P005', '木材', '建筑材料', ' pine 2x4', 1200.00, 900.00, 50, 'ACTIVE');

-- 插入供应商数据
INSERT INTO supplier (supplier_name, contact_person, phone, email, address, status) VALUES
('供应商A', '王经理', '13900139001', 'wang@supplierA.com', '北京市朝阳区', 'ACTIVE'),
('供应商B', '李经理', '13900139002', 'li@supplierB.com', '上海市浦东新区', 'ACTIVE'),
('供应商C', '张经理', '13900139003', 'zhang@supplierC.com', '广州市天河区', 'ACTIVE');

-- 插入采购订单数据
INSERT INTO purchase_order (order_id, supplier_name, material_code, material_name, quantity, unit_price, total_amount, order_date, delivery_date, status) VALUES
('PO001', '供应商A', 'M001', '水泥', 100, 45.00, 4500.00, '2024-01-01', '2024-01-05', '已完成'),
('PO002', '供应商B', 'M002', '钢筋', 50, 5000.00, 250000.00, '2024-01-02', '2024-01-06', '已完成'),
('PO003', '供应商C', 'M003', '木材', 30, 1200.00, 36000.00, '2024-01-03', '2024-01-07', '生产中'),
('PO004', '供应商A', 'M001', '水泥', 150, 44.50, 6675.00, '2024-01-04', '2024-01-08', '已完成'),
('PO005', '供应商B', 'M002', '钢筋', 80, 4950.00, 396000.00, '2024-01-05', '2024-01-09', '已完成');

-- 插入采购价格历史数据
INSERT INTO purchase_price_history (material_code, material_name, supplier_name, price, purchase_date) VALUES
('M001', '水泥', '供应商A', 45.00, '2024-01-01'),
('M002', '钢筋', '供应商B', 5000.00, '2024-01-02'),
('M003', '木材', '供应商C', 1200.00, '2024-01-03'),
('M001', '水泥', '供应商A', 44.50, '2024-01-04'),
('M002', '钢筋', '供应商B', 4950.00, '2024-01-05');

-- 插入产品BOM数据
INSERT INTO product_bom (product_code, product_name, material_code, material_name, quantity, unit) VALUES
('P001', '乳胶漆', 'M004', '乳液', 15.00, 'L'),
('P001', '乳胶漆', 'M005', '颜料', 2.00, 'kg'),
('P001', '乳胶漆', 'M006', '添加剂', 0.50, 'kg'),
('P002', '瓷砖', 'M007', '瓷土', 8.00, 'kg'),
('P002', '瓷砖', 'M008', '釉料', 1.00, 'kg');

-- 插入生产订单数据
INSERT INTO production_order (order_no, product_code, product_name, plan_quantity, actual_quantity, start_time, end_time, status) VALUES
('MO001', 'P001', '乳胶漆', 500, 500, '2024-01-01 08:00', '2024-01-01 18:00', '已完成'),
('MO002', 'P002', '瓷砖', 1000, 980, '2024-01-02 08:00', '2024-01-02 18:00', '已完成'),
('MO003', 'P003', '水泥', 2000, 2000, '2024-01-03 08:00', '2024-01-03 18:00', '已完成'),
('MO004', 'P001', '乳胶漆', 300, 300, '2024-01-04 08:00', '2024-01-04 18:00', '已完成'),
('MO005', 'P002', '瓷砖', 800, 780, '2024-01-05 08:00', '2024-01-05 18:00', '已完成');

-- 插入制造费用明细数据
INSERT INTO manufacturing_cost_detail (product_code, product_name, cost_type, cost_amount, cost_date) VALUES
('P001', '乳胶漆', '原材料', 10000.00, '2024-01'),
('P001', '乳胶漆', '人工', 5000.00, '2024-01'),
('P001', '乳胶漆', '设备折旧', 2000.00, '2024-01'),
('P002', '瓷砖', '原材料', 15000.00, '2024-01'),
('P002', '瓷砖', '人工', 8000.00, '2024-01'),
('P002', '瓷砖', '设备折旧', 3000.00, '2024-01'),
('P003', '水泥', '原材料', 20000.00, '2024-01'),
('P003', '水泥', '人工', 6000.00, '2024-01'),
('P003', '水泥', '设备折旧', 4000.00, '2024-01');

-- 插入预算执行数据
INSERT INTO budget_execution (department, budget_item, budget_amount, actual_amount, variance, variance_rate, month) VALUES
('生产部', '原材料', 100000.00, 95000.00, 5000.00, 5.00, '2024-01'),
('生产部', '人工', 50000.00, 52000.00, -2000.00, -4.00, '2024-01'),
('生产部', '设备', 30000.00, 28000.00, 2000.00, 6.67, '2024-01'),
('销售部', '广告', 20000.00, 18000.00, 2000.00, 10.00, '2024-01'),
('销售部', '差旅', 15000.00, 16000.00, -1000.00, -6.67, '2024-01'),
('行政部', '办公', 10000.00, 9500.00, 500.00, 5.00, '2024-01'),
('行政部', '福利', 8000.00, 8000.00, 0.00, 0.00, '2024-01');

-- 插入内部控制日志数据
INSERT INTO internal_control_log (alert_type, alert_level, alert_message, related_data, processing_status) VALUES
('预算超支', '警告', '销售部差旅费超支', '{"department":"销售部","item":"差旅费","budget":15000,"actual":16000}', '已处理'),
('库存不足', '严重', '乳胶漆库存低于安全水平', '{"product":"P001","current":10,"safety":50}', '处理中'),
('价格波动', '信息', '钢筋价格下降', '{"material":"钢筋","oldPrice":5000,"newPrice":4950}', '已处理'),
('生产延迟', '警告', '瓷砖生产延迟', '{"order":"MO005","plan":1000,"actual":780}', '已处理'),
('供应商异常', '严重', '供应商A供货延迟', '{"supplier":"供应商A","order":"PO001","delay":2}', '处理中');

-- 插入审批状态数据
INSERT INTO approval_status (order_id, order_type, status, approver, comments) VALUES
('PO001', '采购订单', '已批准', 'admin', '同意采购'),
('PO002', '采购订单', '已批准', 'admin', '同意采购'),
('PO003', '采购订单', '待审批', 'user1', NULL),
('MO001', '生产订单', '已批准', 'user2', '同意生产'),
('MO002', '生产订单', '已批准', 'user2', '同意生产');

-- 插入工作流定义数据
INSERT INTO workflow_definition (workflow_name, workflow_key, description, status) VALUES
('采购审批流程', 'purchase_approval', '采购订单审批流程', 'ACTIVE'),
('生产审批流程', 'production_approval', '生产订单审批流程', 'ACTIVE'),
('报销审批流程', 'expense_approval', '费用报销审批流程', 'ACTIVE');

-- 插入工作流节点数据
INSERT INTO workflow_node (workflow_id, node_name, node_type, assignee, sequence) VALUES
(1, '部门经理审批', 'approval', 'user1', 1),
(1, '财务审批', 'approval', 'user2', 2),
(1, '总经理审批', 'approval', 'admin', 3),
(2, '生产经理审批', 'approval', 'user1', 1),
(2, '技术总监审批', 'approval', 'user2', 2),
(3, '部门经理审批', 'approval', 'user1', 1),
(3, '财务审批', 'approval', 'user2', 2);

-- 插入工作流实例数据
INSERT INTO workflow_instance (workflow_id, instance_id, business_key, status, create_time, end_time) VALUES
(1, 'WI001', 'PO001', 'COMPLETED', '2024-01-01 10:00:00', '2024-01-01 14:00:00'),
(1, 'WI002', 'PO002', 'COMPLETED', '2024-01-02 10:00:00', '2024-01-02 15:00:00'),
(1, 'WI003', 'PO003', 'RUNNING', '2024-01-03 10:00:00', NULL),
(2, 'WI004', 'MO001', 'COMPLETED', '2024-01-01 09:00:00', '2024-01-01 11:00:00'),
(2, 'WI005', 'MO002', 'COMPLETED', '2024-01-02 09:00:00', '2024-01-02 11:30:00');

-- 插入工作流任务数据
INSERT INTO workflow_task (instance_id, task_name, assignee, status, create_time, complete_time, comments) VALUES
(1, '部门经理审批', 'user1', 'COMPLETED', '2024-01-01 10:00:00', '2024-01-01 11:00:00', '同意'),
(1, '财务审批', 'user2', 'COMPLETED', '2024-01-01 11:00:00', '2024-01-01 12:00:00', '同意'),
(1, '总经理审批', 'admin', 'COMPLETED', '2024-01-01 12:00:00', '2024-01-01 14:00:00', '同意'),
(2, '部门经理审批', 'user1', 'COMPLETED', '2024-01-02 10:00:00', '2024-01-02 11:30:00', '同意'),
(2, '财务审批', 'user2', 'COMPLETED', '2024-01-02 11:30:00', '2024-01-02 13:00:00', '同意'),
(2, '总经理审批', 'admin', 'COMPLETED', '2024-01-02 13:00:00', '2024-01-02 15:00:00', '同意'),
(3, '部门经理审批', 'user1', 'PENDING', '2024-01-03 10:00:00', NULL, NULL),
(4, '生产经理审批', 'user1', 'COMPLETED', '2024-01-01 09:00:00', '2024-01-01 09:30:00', '同意'),
(4, '技术总监审批', 'user2', 'COMPLETED', '2024-01-01 09:30:00', '2024-01-01 11:00:00', '同意'),
(5, '生产经理审批', 'user1', 'COMPLETED', '2024-01-02 09:00:00', '2024-01-02 09:45:00', '同意'),
(5, '技术总监审批', 'user2', 'COMPLETED', '2024-01-02 09:45:00', '2024-01-02 11:30:00', '同意');