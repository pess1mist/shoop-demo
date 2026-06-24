USE shuju;

UPDATE workflow_definition SET nodes = '[{\"nodeKey\":\"start\",\"nodeName\":\"开始\",\"nodeType\":\"START\"},{\"nodeKey\":\"dept_approve\",\"nodeName\":\"部门经理审批\",\"nodeType\":\"APPROVAL\",\"approverRole\":\"DEPT_MANAGER\"},{\"nodeKey\":\"finance_approve\",\"nodeName\":\"财务经理审批\",\"nodeType\":\"APPROVAL\",\"approverRole\":\"FINANCE_MANAGER\"},{\"nodeKey\":\"end\",\"nodeName\":\"结束\",\"nodeType\":\"END\"}]' WHERE process_key = 'PURCHASE_PLAN';

UPDATE workflow_definition SET nodes = '[{\"nodeKey\":\"start\",\"nodeName\":\"开始\",\"nodeType\":\"START\"},{\"nodeKey\":\"dept_approve\",\"nodeName\":\"部门经理审批\",\"nodeType\":\"APPROVAL\",\"approverRole\":\"DEPT_MANAGER\"},{\"nodeKey\":\"finance_approve\",\"nodeName\":\"财务经理审批\",\"nodeType\":\"APPROVAL\",\"approverRole\":\"FINANCE_MANAGER\"},{\"nodeKey\":\"end\",\"nodeName\":\"结束\",\"nodeType\":\"END\"}]' WHERE process_key = 'BUDGET_ADJUST';

UPDATE workflow_definition SET nodes = '[{\"nodeKey\":\"start\",\"nodeName\":\"开始\",\"nodeType\":\"START\"},{\"nodeKey\":\"sales_approve\",\"nodeName\":\"销售经理审批\",\"nodeType\":\"APPROVAL\",\"approverRole\":\"SALES\"},{\"nodeKey\":\"end\",\"nodeName\":\"结束\",\"nodeType\":\"END\"}]' WHERE process_key = 'SALES_ORDER';
