SET NAMES utf8mb4;
USE shuju;
DELETE FROM internal_control_log;
INSERT INTO internal_control_log (log_id, alert_type, related_doc_no, alert_time, alert_content, handle_status, handler) VALUES
('LOG202501001', 'Budget Overrun', 'PO202501001', '2025-03-05 10:30:00', '采购订单PO202501001材料费用超出预算15%，预算金额1340769.43元，实际金额1542884.82元', 'unhandled', NULL),
('LOG202501002', 'Budget Overrun', 'PO202502015', '2025-03-08 14:20:00', '采购订单PO202502015人工费用超出预算12%，预算金额206458.58元，实际金额231233.61元', 'unhandled', NULL),
('LOG202501003', 'Budget Overrun', 'PO202503001', '2025-03-15 09:45:00', '采购订单PO202503001制造费用超出预算8%，预算金额269189.03元，实际金额290724.15元', 'processing', '张经理');