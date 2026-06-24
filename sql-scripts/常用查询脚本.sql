-- ========================================
-- 常用数据库查询脚本
-- 使用方法：在 MySQL 客户端中执行
-- ========================================

-- 1. 查看所有表
SHOW TABLES;

-- 2. 查看表结构（替换 table_name 为实际表名）
-- DESCRIBE table_name;

-- 3. 查看内控预警数据
SELECT 
    log_id AS '日志ID',
    alert_type AS '预警类型',
    related_doc_no AS '相关单据',
    alert_time AS '预警时间',
    LEFT(alert_content, 80) AS '预警内容',
    handle_status AS '处理状态',
    handler AS '处理人'
FROM internal_control_log 
ORDER BY alert_time DESC 
LIMIT 20;

-- 4. 统计各预警类型数量
SELECT 
    alert_type AS '预警类型',
    COUNT(*) AS '数量',
    handle_status AS '处理状态'
FROM internal_control_log 
GROUP BY alert_type, handle_status
ORDER BY alert_type;

-- 5. 查看用户列表
SELECT 
    user_id AS '用户ID',
    username AS '用户名',
    real_name AS '真实姓名',
    role AS '角色',
    status AS '状态'
FROM user;

-- 6. 查看采购订单（最近10条）
SELECT 
    order_no AS '订单号',
    supplier_name AS '供应商',
    total_amount AS '总金额',
    status AS '状态',
    create_time AS '创建时间'
FROM purchase_order 
ORDER BY create_time DESC 
LIMIT 10;

-- 7. 查看生产订单（最近10条）
SELECT 
    order_no AS '订单号',
    product_name AS '产品名称',
    quantity AS '数量',
    status AS '状态',
    create_time AS '创建时间'
FROM production_order 
ORDER BY create_time DESC 
LIMIT 10;

-- 8. 查看预算执行情况
SELECT 
    budget_code AS '预算编码',
    budget_name AS '预算名称',
    budget_amount AS '预算金额',
    used_amount AS '已用金额',
    remaining_amount AS '剩余金额'
FROM budget_execution 
LIMIT 10;

-- 9. 查看工作流任务
SELECT 
    task_id AS '任务ID',
    task_name AS '任务名称',
    assignee_name AS '处理人',
    status AS '状态',
    create_time AS '创建时间'
FROM workflow_task 
ORDER BY create_time DESC 
LIMIT 10;

-- 10. 查看供应商信息
SELECT 
    supplier_id AS '供应商ID',
    supplier_name AS '供应商名称',
    contact_person AS '联系人',
    phone AS '电话',
    status AS '状态'
FROM supplier 
LIMIT 10;

-- ========================================
-- 数据统计查询
-- ========================================

-- 统计各表记录数
SELECT 'internal_control_log' AS '表名', COUNT(*) AS '记录数' FROM internal_control_log
UNION ALL
SELECT 'user', COUNT(*) FROM user
UNION ALL
SELECT 'purchase_order', COUNT(*) FROM purchase_order
UNION ALL
SELECT 'production_order', COUNT(*) FROM production_order
UNION ALL
SELECT 'supplier', COUNT(*) FROM supplier
UNION ALL
SELECT 'workflow_task', COUNT(*) FROM workflow_task
ORDER BY '记录数' DESC;
