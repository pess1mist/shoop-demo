USE shuju;

-- 更新销售订单审批流程的节点为中文
UPDATE workflow_definition 
SET nodes = '[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"sales_approve","nodeName":"销售经理审批","nodeType":"APPROVAL","approverRole":"SALES"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]'
WHERE process_key = 'SALES_ORDER';
