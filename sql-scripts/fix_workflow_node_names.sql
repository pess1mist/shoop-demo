USE shuju;

-- ============================================
-- 修复流程节点名称为中文
-- ============================================

-- 更新采购计划审批流程的节点名称
UPDATE workflow_node SET node_name = '提交申请' WHERE node_key = 'start' AND definition_id = (SELECT id FROM workflow_definition WHERE process_key = 'PURCHASE_PLAN');
UPDATE workflow_node SET node_name = '部门经理审批' WHERE node_key = 'dept_approve' AND definition_id = (SELECT id FROM workflow_definition WHERE process_key = 'PURCHASE_PLAN');
UPDATE workflow_node SET node_name = '财务审批' WHERE node_key = 'finance_approve' AND definition_id = (SELECT id FROM workflow_definition WHERE process_key = 'PURCHASE_PLAN');
UPDATE workflow_node SET node_name = '审批完成' WHERE node_key = 'end' AND definition_id = (SELECT id FROM workflow_definition WHERE process_key = 'PURCHASE_PLAN');

-- ============================================
-- 修复已有的任务记录名称（针对历史数据）
-- ============================================

-- 更新所有 dept_approve 任务的名称
UPDATE workflow_task SET node_name = '部门经理审批' WHERE node_key = 'dept_approve';

-- 更新所有 finance_approve 任务的名称  
UPDATE workflow_task SET node_name = '财务审批' WHERE node_key = 'finance_approve';

-- 更新所有 start 任务的名称
UPDATE workflow_task SET node_name = '提交申请' WHERE node_key = 'start';

-- 更新所有 end 任务的名称
UPDATE workflow_task SET node_name = '审批完成' WHERE node_key = 'end';

-- ============================================
-- 验证结果
-- ============================================

SELECT '节点名称已更新为中文：' AS info;
SELECT node_key, node_name FROM workflow_node WHERE definition_id IN (SELECT id FROM workflow_definition WHERE process_key = 'PURCHASE_PLAN') ORDER BY sort_order;

SELECT '任务名称已更新为中文：' AS info;
SELECT DISTINCT node_key, node_name FROM workflow_task ORDER BY node_key;
