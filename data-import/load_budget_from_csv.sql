SET NAMES utf8mb4;
USE shuju;
DELETE FROM budget_execution;

LOAD DATA INFILE '/var/lib/mysql-files/budget_execution_base.csv'
INTO TABLE budget_execution
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, fiscal_year, period, budget_item, budget_amount, actual_amount, variance, variance_rate);
