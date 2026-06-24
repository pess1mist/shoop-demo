USE shuju;

UPDATE workflow_instance 
SET started_by = 4, initiator_name = 'Buyer Wang' 
WHERE business_type IN ('PURCHASE', 'PURCHASE_PLAN');

UPDATE workflow_instance 
SET started_by = 3, initiator_name = 'Finance Li' 
WHERE business_type IN ('BUDGET', 'BUDGET_ADJUST');

UPDATE workflow_instance 
SET started_by = 1, initiator_name = 'Admin' 
WHERE business_type IN ('ALERT', 'ALERT_WORKORDER');

UPDATE workflow_instance 
SET started_by = 5, initiator_name = 'Sales Zhao' 
WHERE business_type IN ('SALES', 'SALES_ORDER');

UPDATE workflow_instance 
SET ended_time = DATE_ADD(started_time, INTERVAL FLOOR(1 + RAND() * 5) DAY)
WHERE status IN ('COMPLETED', '已完成') AND ended_time IS NULL;

UPDATE workflow_instance 
SET started_time = DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)
WHERE started_time < DATE_SUB(NOW(), INTERVAL 60 DAY);
