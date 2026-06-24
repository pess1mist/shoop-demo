# 修复流程定义中文数据
$mysqlPath = "mysql"
$sqlScript = @"
USE shuju;

-- 1. 更新采购审批流程
UPDATE workflow_definition 
SET process_name = '采购审批流程', 
    nodes = CONCAT('[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]')
WHERE process_key = 'PURCHASE_PLAN';

-- 2. 更新预算调整流程
UPDATE workflow_definition 
SET process_name = '预算调整流程', 
    nodes = CONCAT('[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"dept_approve","nodeName":"部门经理审批","nodeType":"APPROVAL","approverRole":"DEPT_MANAGER"},{"nodeKey":"finance_approve","nodeName":"财务经理审批","nodeType":"APPROVAL","approverRole":"FINANCE_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]')
WHERE process_key = 'BUDGET_ADJUST';

-- 3. 更新销售订单审批流程
UPDATE workflow_definition 
SET process_name = '销售订单审批流程', 
    nodes = CONCAT('[{"nodeKey":"start","nodeName":"开始","nodeType":"START"},{"nodeKey":"sales_approve","nodeName":"销售经理审批","nodeType":"APPROVAL","approverRole":"SALES_MANAGER"},{"nodeKey":"end","nodeName":"结束","nodeType":"END"}]')
WHERE process_key = 'SALES_ORDER';

-- 4. 验证更新结果
SELECT id, process_key, process_name, version, status FROM workflow_definition ORDER BY id;
"@

# 设置 UTF-8 编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 执行 SQL
$sqlScript | & $mysqlPath -u root -p123456 --default-character-set=utf8mb4 shuju
