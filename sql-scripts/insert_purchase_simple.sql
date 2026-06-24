-- Insert purchase plans (5 records)
INSERT INTO purchase_plan (plan_code, plan_name, dept_code, dept_name, supplier_code, supplier_name, material_code, material_name, quantity, unit_price, total_amount, plan_date, require_date, status, created_by, created_by_name, created_time) VALUES
('PP202604001', 'April Puffed Line Materials', 'LINE1', 'Puffed Line', 'S01', 'Supplier A', 'M001', 'Wheat Flour', 50, 2800.00, 140000.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'PENDING', 1, 'Zhang San', NOW()),
('PP202604002', 'April Emulsion Line Materials', 'LINE2', 'Emulsion Line', 'S02', 'Supplier B', 'M002', 'Vegetable Oil', 30, 5200.00, 156000.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'APPROVED', 2, 'Li Si', NOW()),
('PP202604003', 'April Adhesive Line Materials', 'LINE3', 'Adhesive Line', 'S03', 'Supplier C', 'M003', 'Adhesive', 20, 8500.00, 170000.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'IN_PROGRESS', 1, 'Zhang San', NOW()),
('PP202603001', 'March Puffed Line Materials', 'LINE1', 'Puffed Line', 'S01', 'Supplier A', 'M001', 'Wheat Flour', 45, 2750.00, 123750.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 23 DAY), 'COMPLETED', 1, 'Zhang San', DATE_SUB(NOW(), INTERVAL 30 DAY)),
('PP202603002', 'March Emulsion Line Materials', 'LINE2', 'Emulsion Line', 'S02', 'Supplier B', 'M002', 'Vegetable Oil', 28, 5150.00, 144200.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'COMPLETED', 2, 'Li Si', DATE_SUB(NOW(), INTERVAL 30 DAY));

-- Insert purchase orders (15 records)
INSERT INTO purchase_order (order_date, supplier_id, material_code, material_name, quantity, unit_price, total_amount, approval_status) VALUES
(CURDATE(), 1, 'M001', 'Wheat Flour', 50, 2800.00, 140000.00, 'PENDING'),
(CURDATE(), 2, 'M002', 'Vegetable Oil', 30, 5200.00, 156000.00, 'APPROVED'),
(CURDATE(), 3, 'M003', 'Adhesive', 20, 8500.00, 170000.00, 'PENDING'),
(DATE_SUB(CURDATE(), INTERVAL 25 DAY), 1, 'M001', 'Wheat Flour', 45, 2750.00, 123750.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 25 DAY), 2, 'M002', 'Vegetable Oil', 28, 5150.00, 144200.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 20 DAY), 3, 'M003', 'Adhesive', 18, 8400.00, 151200.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 15 DAY), 1, 'M001', 'Wheat Flour', 48, 2780.00, 133440.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 12 DAY), 2, 'M002', 'Vegetable Oil', 32, 5180.00, 165760.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 10 DAY), 3, 'M003', 'Adhesive', 22, 8450.00, 185900.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 45 DAY), 1, 'M001', 'Wheat Flour', 42, 2720.00, 114240.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 40 DAY), 2, 'M002', 'Vegetable Oil', 25, 5100.00, 127500.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 35 DAY), 3, 'M003', 'Adhesive', 19, 8350.00, 158650.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 30 DAY), 1, 'M001', 'Wheat Flour', 46, 2740.00, 126040.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 28 DAY), 2, 'M002', 'Vegetable Oil', 29, 5120.00, 148480.00, 'APPROVED'),
(DATE_SUB(CURDATE(), INTERVAL 25 DAY), 3, 'M003', 'Adhesive', 21, 8380.00, 175980.00, 'APPROVED');

-- Insert material price fluctuations (30 records)
INSERT INTO material_price_fluctuation (material_code, material_name, price_date, unit_price, previous_price, change_amount, change_rate, fluctuation_level, remark) VALUES
('M001', 'Wheat Flour', CURDATE(), 2800.00, 2750.00, 50.00, 1.82, 'LOW', 'Slight increase'),
('M001', 'Wheat Flour', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 2750.00, 2700.00, 50.00, 1.85, 'LOW', 'Normal fluctuation'),
('M001', 'Wheat Flour', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 2700.00, 2680.00, 20.00, 0.75, 'LOW', 'Stable'),
('M001', 'Wheat Flour', DATE_SUB(CURDATE(), INTERVAL 90 DAY), 2680.00, 2650.00, 30.00, 1.13, 'LOW', 'Slight increase'),
('M001', 'Wheat Flour', DATE_SUB(CURDATE(), INTERVAL 120 DAY), 2650.00, 2640.00, 10.00, 0.38, 'LOW', 'Stable'),
('M002', 'Vegetable Oil', CURDATE(), 5200.00, 5100.00, 100.00, 1.96, 'MEDIUM', 'Price increase'),
('M002', 'Vegetable Oil', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 5100.00, 5050.00, 50.00, 0.99, 'LOW', 'Slight increase'),
('M002', 'Vegetable Oil', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 5050.00, 5000.00, 50.00, 1.00, 'LOW', 'Normal fluctuation'),
('M002', 'Vegetable Oil', DATE_SUB(CURDATE(), INTERVAL 90 DAY), 5000.00, 4950.00, 50.00, 1.01, 'LOW', 'Slight increase'),
('M002', 'Vegetable Oil', DATE_SUB(CURDATE(), INTERVAL 120 DAY), 4950.00, 4920.00, 30.00, 0.61, 'LOW', 'Stable'),
('M003', 'Adhesive', CURDATE(), 8500.00, 8300.00, 200.00, 2.41, 'MEDIUM', 'Significant increase'),
('M003', 'Adhesive', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 8300.00, 8200.00, 100.00, 1.22, 'LOW', 'Normal fluctuation'),
('M003', 'Adhesive', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 8200.00, 8150.00, 50.00, 0.61, 'LOW', 'Slight increase'),
('M003', 'Adhesive', DATE_SUB(CURDATE(), INTERVAL 90 DAY), 8150.00, 8100.00, 50.00, 0.62, 'LOW', 'Normal fluctuation'),
('M003', 'Adhesive', DATE_SUB(CURDATE(), INTERVAL 120 DAY), 8100.00, 8050.00, 50.00, 0.62, 'LOW', 'Slight increase');
