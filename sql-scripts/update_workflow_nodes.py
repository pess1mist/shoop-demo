import pymysql
import json

# 连接数据库
connection = pymysql.connect(
    host='localhost',
    port=3306,
    user='root',
    password='123456',
    database='shuju',
    charset='utf8mb4'
)

try:
    with connection.cursor() as cursor:
        # 更新采购计划审批流程
        purchase_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "dept_approve", "nodeName": "部门经理审批", "nodeType": "APPROVAL", "approverRole": "DEPT_MANAGER"},
            {"nodeKey": "finance_approve", "nodeName": "财务经理审批", "nodeType": "APPROVAL", "approverRole": "FINANCE_MANAGER"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'PURCHASE_PLAN'"
        cursor.execute(sql, (json.dumps(purchase_nodes, ensure_ascii=False),))
        print(f"更新采购计划: {cursor.rowcount} 行")
        
        # 更新预算调整审批流程
        budget_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "dept_approve", "nodeName": "部门经理审批", "nodeType": "APPROVAL", "approverRole": "DEPT_MANAGER"},
            {"nodeKey": "finance_approve", "nodeName": "财务经理审批", "nodeType": "APPROVAL", "approverRole": "FINANCE_MANAGER"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'BUDGET_ADJUST'"
        cursor.execute(sql, (json.dumps(budget_nodes, ensure_ascii=False),))
        print(f"更新预算调整: {cursor.rowcount} 行")
        
        # 更新销售订单审批流程
        sales_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "sales_approve", "nodeName": "销售经理审批", "nodeType": "APPROVAL", "approverRole": "SALES"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'SALES_ORDER'"
        cursor.execute(sql, (json.dumps(sales_nodes, ensure_ascii=False),))
        print(f"更新销售订单: {cursor.rowcount} 行")
        
        connection.commit()
        
        # 验证更新结果
        cursor.execute("SELECT process_key, nodes FROM workflow_definition")
        results = cursor.fetchall()
        print("\n验证结果:")
        for row in results:
            process_key = row[0]
            nodes = json.loads(row[1])
            node_names = [n['nodeName'] for n in nodes]
            print(f"{process_key}: {' → '.join(node_names)}")
            
finally:
    connection.close()
