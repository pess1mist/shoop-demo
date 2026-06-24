DELETE FROM product;
INSERT INTO product (product_code, product_name, unit, unit_price, cost_price) VALUES ('P01', '膨化硝铵炸药', '吨', 5000, 3750);
INSERT INTO product (product_code, product_name, unit, unit_price, cost_price) VALUES ('P02', '乳化炸药', '吨', 6000, 4500);
INSERT INTO product (product_code, product_name, unit, unit_price, cost_price) VALUES ('P03', '水胶炸药', '吨', 4000, 3000);
INSERT INTO supplier (supplier_id, supplier_name, credit_grade) VALUES ('S01', '化工原料公司', 'A');
INSERT INTO supplier (supplier_id, supplier_name, credit_grade) VALUES ('S02', '乳化剂供应商', 'B');
INSERT INTO supplier (supplier_id, supplier_name, credit_grade) VALUES ('S03', '添加剂厂', 'A');

