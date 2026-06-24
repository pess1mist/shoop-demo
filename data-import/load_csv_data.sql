-- Import all CSV data to shuju database
USE shuju;

-- Clear existing example data
DELETE FROM production_order;
DELETE FROM product;
DELETE FROM supplier;
DELETE FROM product_bom;
DELETE FROM budget_execution;
DELETE FROM manufacturing_cost_detail;
DELETE FROM purchase_price_history;
DELETE FROM purchase_order;

-- ========================================
-- 1. Import products
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/product_code.csv'
INTO TABLE `product`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_code, product_name, unit);

-- ========================================
-- 2. Import suppliers
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/supplier.csv'
INTO TABLE `supplier`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(supplier_id, supplier_name, credit_grade);

-- ========================================
-- 3. Import production orders
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/production_order.csv'
INTO TABLE `production_order`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(prod_order_id, product_code, @plan_qty, @actual_qty, start_date, end_date, @mat_cost, @lab_cost, @manu_cost, @total_cost)
SET 
    plan_quantity = CAST(@plan_qty AS UNSIGNED),
    actual_quantity = CAST(@actual_qty AS UNSIGNED),
    material_cost = CAST(@mat_cost AS UNSIGNED),
    labor_cost = CAST(@lab_cost AS UNSIGNED),
    manu_cost = CAST(@manu_cost AS UNSIGNED),
    total_cost = CAST(@total_cost AS UNSIGNED),
    status = 'completed';

-- ========================================
-- 4. Import product BOM
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/product_bom.csv'
INTO TABLE `product_bom`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(bom_id, product_code, product_name, material_code, material_name, @qty_per_unit, valid_from)
SET quantity_per_unit = CAST(@qty_per_unit AS DECIMAL(10,2));

-- ========================================
-- 5. Import budget execution
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/budget_execution_base.csv'
INTO TABLE `budget_execution`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, fiscal_year, period, budget_item, @budget_amt, @actual_amt, @variance, @var_rate)
SET 
    budget_amount = CAST(@budget_amt AS DECIMAL(15,2)),
    actual_amount = CAST(@actual_amt AS DECIMAL(15,2)),
    variance = CAST(@variance AS DECIMAL(15,2)),
    variance_rate = CAST(@var_rate AS DECIMAL(10,2));

-- ========================================
-- 6. Import manufacturing cost detail
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/manufacturing_cost_detail.csv'
INTO TABLE `manufacturing_cost_detail`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, cost_date, cost_category, @amt, production_line, approver)
SET amount = CAST(@amt AS DECIMAL(15,2));

-- ========================================
-- 7. Import purchase price history
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/purchase_price_history.csv'
INTO TABLE `purchase_price_history`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, material_code, order_date, supplier_id, @unit_price, remark)
SET unit_price = CAST(@unit_price AS DECIMAL(10,2));

-- ========================================
-- 8. Import purchase orders from approval_status
-- ========================================
LOAD DATA LOCAL INFILE 'C:/Users/19012/Desktop/2/approval_status.csv'
INTO TABLE `purchase_order`
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(order_id, order_date, supplier_id, material_code, material_name, @qty, @unit_price, @total_amt, @approval_status)
SET 
    quantity = CAST(@qty AS DECIMAL(10,2)),
    unit_price = CAST(@unit_price AS DECIMAL(10,2)),
    total_amount = CAST(@total_amt AS DECIMAL(15,2)),
    approval_status = CASE WHEN @approval_status = '已批' THEN 'approved' ELSE 'pending' END;
