-- 修复 product 表字段名
USE test;

-- 将 product_id 改为 product_code
ALTER TABLE product 
CHANGE COLUMN `product_id` product_code VARCHAR(20) NOT NULL COMMENT '产品代码';

-- 添加 unit 字段
ALTER TABLE product 
ADD COLUMN unit VARCHAR(20) COMMENT '单位' AFTER product_name;

-- 更新单位数据
UPDATE product SET unit = '吨' WHERE product_code IN ('P01', 'P02', 'P03');

-- 验证修改后的表结构
DESCRIBE product;

-- 查看修改后的数据
SELECT * FROM product;
