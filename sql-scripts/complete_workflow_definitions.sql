USE shuju;

-- 补充流程定义的完整信息

-- 1. 采购计划审批流程
UPDATE workflow_definition 
SET nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]',
    created_time = '2024-01-15 10:00:00'
WHERE process_key = 'PURCHASE_PLAN';

-- 2. 预算调整审批流程  
UPDATE workflow_definition 
SET nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]',
    created_time = '2024-01-15 10:00:00'
WHERE process_key = 'BUDGET_ADJUST';

-- 3. 销售订单审批流程
UPDATE workflow_definition 
SET nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"sales_approve","nodeName":"销售经理审批","nodeType":"APPROVAL","approverRole":"SALES"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]',
    created_time = '2024-02-20 14:30:00'
WHERE process_key = 'SALES_ORDER';
