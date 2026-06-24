CREATE TABLE IF NOT EXISTS sales_order (
    order_id VARCHAR(20) PRIMARY KEY,
    order_date DATE,
    customer_id VARCHAR(10),
    product_code VARCHAR(10),
    product_name VARCHAR(100),
    quantity DECIMAL(10,2),
    unit_price DECIMAL(10,2),
    total_amount DECIMAL(15,2),
    approval_status VARCHAR(20),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sales_order VALUES 
('SO202301001', '2023-06-30', 'C01', 'P01', '水胶线', 5000.00, 5700.00, 28500000.00, 'approved', NOW(), NOW()),
('SO202301002', '2023-12-31', 'C01', 'P01', '水胶线', 4500.00, 5700.00, 25650000.00, 'approved', NOW(), NOW()),
('SO202401001', '2024-06-30', 'C01', 'P01', '水胶线', 3878.00, 5700.00, 22104600.00, 'approved', NOW(), NOW());

INSERT INTO sales_order VALUES 
('SO202301003', '2023-06-30', 'C02', 'P02', '膨化线', 3000.00, 6900.00, 20700000.00, 'approved', NOW(), NOW()),
('SO202301004', '2023-12-31', 'C02', 'P02', '膨化线', 2500.00, 6900.00, 17250000.00, 'approved', NOW(), NOW()),
('SO202401001', '2024-06-30', 'C02', 'P02', '膨化线', 1875.00, 6900.00, 12937500.00, 'approved', NOW(), NOW());

INSERT INTO sales_order VALUES 
('SO202301005', '2023-06-30', 'C03', 'P03', '乳化线', 2000.00, 4700.00, 9400000.00, 'approved', NOW(), NOW()),
('SO202301006', '2023-12-31', 'C03', 'P03', '乳化线', 1800.00, 4700.00, 8460000.00, 'approved', NOW(), NOW()),
('SO202401002', '2024-06-30', 'C03', 'P03', '乳化线', 1220.00, 4700.00, 5734000.00, 'approved', NOW(), NOW());

SELECT 'Sales orders created successfully!' as result;
SELECT SUM(total_amount)/10000 as total_revenue_wan FROM sales_order;
