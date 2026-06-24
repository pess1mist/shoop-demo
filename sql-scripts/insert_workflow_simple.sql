-- Insert workflow instances
INSERT INTO workflow_instance (process_id, process_key, business_type, business_key, title, initiator_id, initiator_name, current_node_key, status, start_time, created_time) VALUES
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO20260420001', 'Purchase Order PO20260420001', 1, 'Zhang San', 'node2', 'RUNNING', NOW(), NOW()),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO20260420002', 'Purchase Order PO20260420002', 2, 'Li Si', 'node3', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO20260420003', 'Purchase Order PO20260420003', 1, 'Zhang San', 'node1', 'RUNNING', NOW(), NOW()),
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA20260420001', 'Budget Adjustment BA20260420001', 1, 'Zhang San', 'node1', 'RUNNING', NOW(), NOW()),
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA20260420002', 'Budget Adjustment BA20260420002', 2, 'Li Si', 'node3', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'PURCHASE_PLAN', 'ALERT', 'AH20260420001', 'Alert Handling AH20260420001', 3, 'Wang Wu', 'node1', 'PENDING', NOW(), NOW()),
(1, 'PURCHASE_PLAN', 'ALERT', 'AH20260420002', 'Alert Handling AH20260420002', 2, 'Manager Li', 'node2', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'PURCHASE_PLAN', 'PURCHASE', 'PO20260419001', 'Purchase Order PO20260419001', 3, 'Wang Wu', 'node2', 'REJECTED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 'BUDGET_ADJUST', 'BUDGET', 'BA20260418001', 'Budget Adjustment BA20260418001', 1, 'Zhang San', 'node3', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 'PURCHASE_PLAN', 'ALERT', 'AH20260417001', 'Alert Handling AH20260417001', 2, 'Manager Li', 'node2', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY));

-- Insert workflow tasks
INSERT INTO workflow_task (instance_id, node_key, node_name, assignee_id, status, action, comment, start_time) VALUES
(1, 'node2', 'Manager Approval', 2, 'PENDING', NULL, NULL, NOW()),
(2, 'node2', 'Manager Approval', 2, 'COMPLETED', 'APPROVE', 'Approved', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'node3', 'Finance Review', 3, 'COMPLETED', 'APPROVE', 'Reviewed and approved', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'node1', 'Submit Application', 1, 'COMPLETED', 'SUBMIT', 'Request raw material purchase', NOW()),
(4, 'node1', 'Submit Application', 1, 'COMPLETED', 'SUBMIT', 'Request budget adjustment', NOW()),
(5, 'node2', 'Manager Approval', 2, 'COMPLETED', 'APPROVE', 'Approved', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 'node3', 'Finance Review', 3, 'COMPLETED', 'APPROVE', 'Budget reasonable, approved', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(6, 'node1', 'Alert Confirmation', 3, 'PENDING', NULL, NULL, NOW()),
(7, 'node1', 'Alert Confirmation', 2, 'COMPLETED', 'CONFIRM', 'Alert confirmed', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 'node2', 'Handle Alert', 2, 'COMPLETED', 'HANDLE', 'Actions taken', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 'node2', 'Manager Approval', 2, 'COMPLETED', 'REJECT', 'Price too high, rejected', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(9, 'node2', 'Manager Approval', 2, 'COMPLETED', 'APPROVE', 'Approved', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(9, 'node3', 'Finance Review', 3, 'COMPLETED', 'APPROVE', 'Approved', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(10, 'node1', 'Alert Confirmation', 2, 'COMPLETED', 'CONFIRM', 'Cost anomaly confirmed', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(10, 'node2', 'Handle Alert', 2, 'COMPLETED', 'HANDLE', 'Production process optimized', DATE_SUB(NOW(), INTERVAL 12 DAY));
