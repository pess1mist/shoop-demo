-- 将预算执行金额四舍五入到千位（去除个位和小数位）
-- 更符合实际业务中的预算精度

UPDATE budget_execution 
SET 
    budget_amount = ROUND(budget_amount / 1000) * 1000,
    actual_amount = ROUND(actual_amount / 1000) * 1000;

-- 重新计算差异（基于四舍五入后的金额）
UPDATE budget_execution 
SET 
    variance = actual_amount - budget_amount,
    variance_rate = ROUND((variance / budget_amount) * 100, 2);

-- 验证结果
SELECT 
    budget_item,
    period,
    budget_amount,
    actual_amount,
    variance,
    variance_rate
FROM budget_execution 
WHERE period = '2025-01'
ORDER BY budget_item;
