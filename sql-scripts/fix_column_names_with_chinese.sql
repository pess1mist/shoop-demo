-- Fix column names with Chinese characters to English
USE test;

-- 1. Fix manufacturing_cost_detail table
ALTER TABLE manufacturing_cost_detail CHANGE `amount(元)` `amount` DECIMAL(10,2);

-- 2. Fix production_order table
ALTER TABLE production_order CHANGE `plan_quantity(吨)` `plan_quantity` DECIMAL(10,2);
ALTER TABLE production_order CHANGE `actual_quantity(吨)` `actual_quantity` DECIMAL(10,2);
ALTER TABLE production_order CHANGE `material_cost(元)` `material_cost` DECIMAL(10,2);
ALTER TABLE production_order CHANGE `labor_cost(元)` `labor_cost` DECIMAL(10,2);
ALTER TABLE production_order CHANGE `manu_cost(元)` `manu_cost` DECIMAL(10,2);
ALTER TABLE production_order CHANGE `total_cost(元)` `total_cost` DECIMAL(10,2);

-- 3. Convert TEXT fields to proper types for production_order
ALTER TABLE production_order 
  MODIFY prod_order_id VARCHAR(20),
  MODIFY product_code VARCHAR(50),
  MODIFY start_date DATE,
  MODIFY end_date DATE;

-- 4. Convert TEXT fields to proper types for manufacturing_cost_detail
ALTER TABLE manufacturing_cost_detail 
  MODIFY cost_date DATE,
  MODIFY cost_category VARCHAR(50),
  MODIFY production_line VARCHAR(50),
  MODIFY approver VARCHAR(50);

-- Verify the changes
SELECT 'Column names fixed!' AS result;
DESCRIBE manufacturing_cost_detail;
DESCRIBE production_order;
