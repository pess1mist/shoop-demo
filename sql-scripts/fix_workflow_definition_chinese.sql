-- ============================================
-- 修复流程定义中文名称
-- 创建时间: 2026-04-21
-- ============================================

USE shuju;

-- 1. 更新采购审批流程
UPDATE workflow_definition 
SET process_name = '采购审批流程', 
    nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]'
WHERE process_key = 'PURCHASE_PLAN';

-- 2. 更新预算调整流程
UPDATE workflow_definition 
SET process_name = '预算调整流程', 
    nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]'
WHERE process_key = 'BUDGET_ADJUST';

-- 3. 更新销售订单审批流程
UPDATE workflow_definition 
SET process_name = '销售订单审批流程', 
    nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"sales_approve","nodeName":"销售经理审批","nodeType":"APPROVAL","approverRole":"SALES_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]'
WHERE process_key = 'SALES_ORDER';

-- 4. 验证更新结果
SELECT id, process_key, process_name, version, status FROM workflow_definition ORDER BY id;

SELECT '流程定义中文修复完成！' as status;
