-- ============================================
-- 创建采购计划表
-- ============================================

CREATE TABLE IF NOT EXISTS purchase_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    plan_code VARCHAR(50) COMMENT '计划单号',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    dept_code VARCHAR(20) COMMENT '部门代码',
    dept_name VARCHAR(50) COMMENT '部门名称',
    supplier_code VARCHAR(20) COMMENT '供应商代码',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    material_code VARCHAR(50) COMMENT '物料代码',
    material_name VARCHAR(100) COMMENT '物料名称',
    quantity INT COMMENT '采购数量',
    unit_price DECIMAL(10, 2) COMMENT '单价',
    total_amount DECIMAL(12, 2) COMMENT '总金额',
    budget_id BIGINT COMMENT '关联预算ID',
    budget_item VARCHAR(50) COMMENT '预算科目',
    plan_date DATE COMMENT '计划日期',
    require_date DATE COMMENT '需求日期',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PENDING-待审批，APPROVED-已批准，REJECTED-已驳回，PURCHASING-采购中，COMPLETED-已完成',
    workflow_instance_id BIGINT COMMENT '工作流实例ID',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人姓名',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    remark TEXT COMMENT '备注',
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_dept_code (dept_code),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划表';

-- 插入测试数据
INSERT INTO purchase_plan (plan_code, plan_name, dept_code, dept_name, supplier_code, supplier_name, material_code, material_name, quantity, unit_price, total_amount, budget_id, budget_item, plan_date, require_date, status, workflow_instance_id, created_by, created_by_name, created_time, remark) VALUES
('PP202401001', '原材料采购-钢材', 'PROD', '生产部', 'SUP001', '宝钢钢铁有限公司', 'MAT001', '优质钢材Q235', 50, 4500.00, 225000.00, 1, 'RAW_MATERIAL', '2024-01-15', '2024-02-01', 'COMPLETED', 1, 1, '张三', '2024-01-15 09:30:00', '生产线急需钢材原料'),
('PP202401002', '设备配件采购', 'PROD', '生产部', 'SUP002', '精工机械制造有限公司', 'MAT002', '高精度轴承', 20, 850.00, 17000.00, 2, 'EQUIPMENT_PARTS', '2024-01-18', '2024-01-25', 'APPROVED', 2, 2, '李四', '2024-01-18 10:15:00', '设备维护所需配件'),
('PP202401003', '办公用品采购', 'ADMIN', '行政部', 'SUP003', '得力文具有限公司', 'MAT003', 'A4打印纸', 100, 35.00, 3500.00, 3, 'OFFICE_SUPPLIES', '2024-01-20', '2024-01-28', 'COMPLETED', 3, 3, '王五', '2024-01-20 14:00:00', '办公室日常消耗品'),
('PP202402001', '电子元器件采购', 'TECH', '技术部', 'SUP004', '华强电子商城', 'MAT004', '集成电路芯片', 200, 120.00, 24000.00, 4, 'R_AND_D', '2024-02-05', '2024-02-15', 'PENDING', NULL, 4, '赵六', '2024-02-05 11:00:00', '研发项目所需电子元件'),
('PP202402002', '化工原料采购', 'PROD', '生产部', 'SUP005', '化工贸易有限公司', 'MAT005', '聚乙烯树脂', 10, 12000.00, 120000.00, 1, 'RAW_MATERIAL', '2024-02-10', '2024-02-20', 'REJECTED', NULL, 1, '张三', '2024-02-10 09:45:00', '预算超支，需重新审批'),
('PP202402003', '包装材料采购', 'MARKET', '市场部', 'SUP006', '包装制品有限公司', 'MAT006', '纸箱包装', 500, 15.00, 7500.00, 5, 'MARKETING', '2024-02-12', '2024-02-18', 'PURCHASING', 4, 5, '孙七', '2024-02-12 16:30:00', '产品包装需求'),
('PP202403001', '生产设备配件', 'PROD', '生产部', 'SUP007', '工业设备有限公司', 'MAT007', '电机配件', 5, 2800.00, 14000.00, 2, 'EQUIPMENT_PARTS', '2024-03-01', '2024-03-10', 'DRAFT', NULL, 2, '李四', '2024-03-01 08:30:00', '设备维修备用件'),
('PP202403002', '安全防护用品', 'ADMIN', '行政部', 'SUP008', '安全用品有限公司', 'MAT008', '防护手套', 300, 25.00, 7500.00, 3, 'OFFICE_SUPPLIES', '2024-03-05', '2024-03-12', 'COMPLETED', 5, 3, '王五', '2024-03-05 10:00:00', '车间安全防护用品'),
('PP202403003', '测试仪器设备', 'TECH', '技术部', 'SUP009', '精密仪器有限公司', 'MAT009', '高精度测试仪表', 2, 45000.00, 90000.00, 4, 'R_AND_D', '2024-03-08', '2024-03-20', 'APPROVED', 6, 4, '赵六', '2024-03-08 14:15:00', '实验室测试设备采购'),
('PP202403004', '营销宣传物料', 'MARKET', '市场部', 'SUP010', '广告有限公司', 'MAT010', '宣传海报', 1000, 8.00, 8000.00, 5, 'MARKETING', '2024-03-10', '2024-03-15', 'COMPLETED', 7, 5, '孙七', '2024-03-10 11:30:00', '新产品宣传物料');

-- 验证数据
SELECT '采购计划表创建成功！' AS result;
SELECT COUNT(*) AS total_records FROM purchase_plan;
SELECT status, COUNT(*) AS count FROM purchase_plan GROUP BY status ORDER BY count DESC;
