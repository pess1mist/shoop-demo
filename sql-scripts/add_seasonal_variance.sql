-- 添加淡旺季波动到预算执行数据
-- 制造业通常：
-- Q1(1-3月): 淡季，执行率较低（春节停产）
-- Q2(4-6月): 旺季开始，逐渐上升
-- Q3(7-9月): 旺季，超支较多（赶工加班）
-- Q4(10-12月): 年末冲刺，波动较大

-- 人工费：按月份添加季节性波动
UPDATE budget_execution SET actual_amount = budget_amount * 0.85 WHERE budget_item = '人工费' AND period LIKE '%-01';
UPDATE budget_execution SET actual_amount = budget_amount * 0.82 WHERE budget_item = '人工费' AND period LIKE '%-02';
UPDATE budget_execution SET actual_amount = budget_amount * 0.88 WHERE budget_item = '人工费' AND period LIKE '%-03';
UPDATE budget_execution SET actual_amount = budget_amount * 0.95 WHERE budget_item = '人工费' AND period LIKE '%-04';
UPDATE budget_execution SET actual_amount = budget_amount * 1.02 WHERE budget_item = '人工费' AND period LIKE '%-05';
UPDATE budget_execution SET actual_amount = budget_amount * 1.08 WHERE budget_item = '人工费' AND period LIKE '%-06';
UPDATE budget_execution SET actual_amount = budget_amount * 1.12 WHERE budget_item = '人工费' AND period LIKE '%-07';
UPDATE budget_execution SET actual_amount = budget_amount * 1.15 WHERE budget_item = '人工费' AND period LIKE '%-08';
UPDATE budget_execution SET actual_amount = budget_amount * 1.10 WHERE budget_item = '人工费' AND period LIKE '%-09';
UPDATE budget_execution SET actual_amount = budget_amount * 1.05 WHERE budget_item = '人工费' AND period LIKE '%-10';
UPDATE budget_execution SET actual_amount = budget_amount * 0.98 WHERE budget_item = '人工费' AND period LIKE '%-11';
UPDATE budget_execution SET actual_amount = budget_amount * 1.08 WHERE budget_item = '人工费' AND period LIKE '%-12';

-- 制造费用：与人工费类似，但波动更大
UPDATE budget_execution SET actual_amount = budget_amount * 0.80 WHERE budget_item = '制造费用' AND period LIKE '%-01';
UPDATE budget_execution SET actual_amount = budget_amount * 0.78 WHERE budget_item = '制造费用' AND period LIKE '%-02';
UPDATE budget_execution SET actual_amount = budget_amount * 0.85 WHERE budget_item = '制造费用' AND period LIKE '%-03';
UPDATE budget_execution SET actual_amount = budget_amount * 0.92 WHERE budget_item = '制造费用' AND period LIKE '%-04';
UPDATE budget_execution SET actual_amount = budget_amount * 1.00 WHERE budget_item = '制造费用' AND period LIKE '%-05';
UPDATE budget_execution SET actual_amount = budget_amount * 1.10 WHERE budget_item = '制造费用' AND period LIKE '%-06';
UPDATE budget_execution SET actual_amount = budget_amount * 1.18 WHERE budget_item = '制造费用' AND period LIKE '%-07';
UPDATE budget_execution SET actual_amount = budget_amount * 1.22 WHERE budget_item = '制造费用' AND period LIKE '%-08';
UPDATE budget_execution SET actual_amount = budget_amount * 1.15 WHERE budget_item = '制造费用' AND period LIKE '%-09';
UPDATE budget_execution SET actual_amount = budget_amount * 1.08 WHERE budget_item = '制造费用' AND period LIKE '%-10';
UPDATE budget_execution SET actual_amount = budget_amount * 0.95 WHERE budget_item = '制造费用' AND period LIKE '%-11';
UPDATE budget_execution SET actual_amount = budget_amount * 1.12 WHERE budget_item = '制造费用' AND period LIKE '%-12';

-- 材料费：相对稳定，但也有季节性（采购策略）
UPDATE budget_execution SET actual_amount = budget_amount * 0.92 WHERE budget_item = '材料费' AND period LIKE '%-01';
UPDATE budget_execution SET actual_amount = budget_amount * 0.90 WHERE budget_item = '材料费' AND period LIKE '%-02';
UPDATE budget_execution SET actual_amount = budget_amount * 0.93 WHERE budget_item = '材料费' AND period LIKE '%-03';
UPDATE budget_execution SET actual_amount = budget_amount * 0.95 WHERE budget_item = '材料费' AND period LIKE '%-04';
UPDATE budget_execution SET actual_amount = budget_amount * 0.90 WHERE budget_item = '材料费' AND period LIKE '%-05';
UPDATE budget_execution SET actual_amount = budget_amount * 0.88 WHERE budget_item = '材料费' AND period LIKE '%-06';
UPDATE budget_execution SET actual_amount = budget_amount * 0.85 WHERE budget_item = '材料费' AND period LIKE '%-07';
UPDATE budget_execution SET actual_amount = budget_amount * 0.87 WHERE budget_item = '材料费' AND period LIKE '%-08';
UPDATE budget_execution SET actual_amount = budget_amount * 0.90 WHERE budget_item = '材料费' AND period LIKE '%-09';
UPDATE budget_execution SET actual_amount = budget_amount * 0.93 WHERE budget_item = '材料费' AND period LIKE '%-10';
UPDATE budget_execution SET actual_amount = budget_amount * 0.95 WHERE budget_item = '材料费' AND period LIKE '%-11';
UPDATE budget_execution SET actual_amount = budget_amount * 0.88 WHERE budget_item = '材料费' AND period LIKE '%-12';

-- 重新计算差异和差异率
UPDATE budget_execution 
SET 
    variance = actual_amount - budget_amount,
    variance_rate = ROUND((variance / budget_amount) * 100, 2);

-- 验证结果：查看每个月的执行率变化
SELECT 
    period,
    budget_item,
    budget_amount,
    actual_amount,
    variance_rate
FROM budget_execution 
WHERE fiscal_year = 2025
ORDER BY budget_item, period;
