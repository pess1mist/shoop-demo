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
        print(f"✓ 更新采购计划审批流程: {cursor.rowcount} 行受影响")
        
        # 更新预算调整审批流程
        budget_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "dept_approve", "nodeName": "部门经理审批", "nodeType": "APPROVAL", "approverRole": "DEPT_MANAGER"},
            {"nodeKey": "finance_approve", "nodeName": "财务经理审批", "nodeType": "APPROVAL", "approverRole": "FINANCE_MANAGER"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'BUDGET_ADJUST'"
        cursor.execute(sql, (json.dumps(budget_nodes, ensure_ascii=False),))
        print(f"✓ 更新预算调整审批流程: {cursor.rowcount} 行受影响")
        
        # 更新销售订单审批流程
        sales_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "sales_approve", "nodeName": "销售经理审批", "nodeType": "APPROVAL", "approverRole": "SALES"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'SALES_ORDER'"
        cursor.execute(sql, (json.dumps(sales_nodes, ensure_ascii=False),))
        print(f"✓ 更新销售订单审批流程: {cursor.rowcount} 行受影响")
        
        # 更新预警工单处理流程
        alert_nodes = [
            {"nodeKey": "start", "nodeName": "开始", "nodeType": "START"},
            {"nodeKey": "confirm", "nodeName": "预警确认", "nodeType": "APPROVAL", "approverRole": "ALERT_HANDLER"},
            {"nodeKey": "handle", "nodeName": "预警处理", "nodeType": "APPROVAL", "approverRole": "ALERT_HANDLER"},
            {"nodeKey": "end", "nodeName": "结束", "nodeType": "END"}
        ]
        
        sql = "UPDATE workflow_definition SET nodes = %s WHERE process_key = 'ALERT_WORKORDER'"
        cursor.execute(sql, (json.dumps(alert_nodes, ensure_ascii=False),))
        print(f"✓ 更新预警工单处理流程: {cursor.rowcount} 行受影响")
        
        connection.commit()
        print("\n✅ 所有流程定义节点已更新为中文！")
        
        # 验证更新结果
        print("\n📊 验证更新结果:")
        cursor.execute("SELECT process_key, nodes FROM workflow_definition")
        results = cursor.fetchall()
        for process_key, nodes_json in results:
            nodes = json.loads(nodes_json)
            approval_nodes = [n for n in nodes if n['nodeType'] == 'APPROVAL']
            node_names = [n['nodeName'] for n in approval_nodes]
            print(f"  {process_key}: {' → '.join(node_names)}")
            
except Exception as e:
    print(f"❌ 更新失败: {e}")
    connection.rollback()
finally:
    connection.close()
