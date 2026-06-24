-- Adjust budget execution variance to be more noticeable

UPDATE budget_execution 
SET actual_amount = budget_amount * 1.08,
    variance = budget_amount * 0.08,
    variance_rate = 8.00
WHERE budget_item = 'Labor';

UPDATE budget_execution 
SET actual_amount = budget_amount * 1.15,
    variance = budget_amount * 0.15,
    variance_rate = 15.00
WHERE budget_item = 'Manufacturing';

UPDATE budget_execution 
SET actual_amount = budget_amount * 0.88,
    variance = budget_amount * -0.12,
    variance_rate = -12.00
WHERE budget_item = 'Material';

-- Verify results
SELECT 
    budget_item,
    SUM(budget_amount) as total_budget,
    SUM(actual_amount) as total_actual,
    SUM(actual_amount) - SUM(budget_amount) as total_variance,
    ROUND((SUM(actual_amount) - SUM(budget_amount)) / SUM(budget_amount) * 100, 2) as variance_percent
FROM budget_execution
GROUP BY budget_item;
