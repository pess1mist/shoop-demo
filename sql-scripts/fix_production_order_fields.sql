USE test;

-- 修复 production_order 表字段名
ALTER TABLE production_order 
  CHANGE `plan_quantity(?)` plan_quantity INT,
  CHANGE `actual_quantity(?)` actual_quantity INT,
  CHANGE `material_cost(?)` material_cost INT,
  CHANGE `labor_cost(?)` labor_cost INT,
  CHANGE `manu_cost(?)` manu_cost INT,
  CHANGE `total_cost(?)` total_cost INT;

-- 添加 status 字段
ALTER TABLE production_order 
  ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT '已完成';
