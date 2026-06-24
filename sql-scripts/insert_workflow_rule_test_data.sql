-- 插入审批规则测试数据
INSERT INTO `workflow_rule` (`rule_name`, `rule_type`, `process_key`, `target_node_key`, `threshold_amount`, `priority`, `status`, `description`) VALUES
('采购计划-小额审批', 'AMOUNT', 'PURCHASE_PLAN', 'DEPT_APPROVE', 50000.00, 1, 'ACTIVE', '采购金额小于5万元，只需部门经理审批'),
('采购计划-中额审批', 'AMOUNT', 'PURCHASE_PLAN', 'FINANCE_APPROVE', 200000.00, 2, 'ACTIVE', '采购金额5万-20万，需要财务审批'),
('采购计划-大额审批', 'AMOUNT', 'PURCHASE_PLAN', 'GM_APPROVE', 500000.00, 3, 'ACTIVE', '采购金额超过20万，需要总经理审批'),
('预算调整-一般审批', 'AMOUNT', 'BUDGET_ADJUST', 'FINANCE_APPROVE', 100000.00, 1, 'ACTIVE', '预算调整金额小于10万，财务审批即可'),
('预算调整-重大审批', 'AMOUNT', 'BUDGET_ADJUST', 'GM_APPROVE', 500000.00, 2, 'ACTIVE', '预算调整金额超过10万，需要总经理审批');
