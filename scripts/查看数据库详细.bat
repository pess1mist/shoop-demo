@echo off
chcp 65001 >nul
echo ========================================
echo   MySQL 数据库详细查看工具
echo ========================================
echo.

:menu
echo 请选择操作：
echo 1. 查看所有表及记录数
echo 2. 查看指定表的完整结构
echo 3. 查看指定表的所有数据
echo 4. 查看内控预警表（internal_control_log）
echo 5. 查看用户表（user）
echo 6. 查看采购订单表（purchase_order）
echo 7. 查看生产订单表（production_order）
echo 8. 查看供应商表（supplier）
echo 9. 查看产品表（product）
echo 10. 查看预算执行表（budget_execution）
echo 11. 查看工作流任务表（workflow_task）
echo 12. 导出表结构到文件
echo 13. 进入 MySQL 命令行
echo 14. 退出
echo.
set /p choice=请输入选项 (1-14): 

if "%choice%"=="1" goto show_all_tables
if "%choice%"=="2" goto describe_table
if "%choice%"=="3" goto view_all_data
if "%choice%"=="4" goto view_internal_control
if "%choice%"=="5" goto view_users
if "%choice%"=="6" goto view_purchase_orders
if "%choice%"=="7" goto view_production_orders
if "%choice%"=="8" goto view_suppliers
if "%choice%"=="9" goto view_products
if "%choice%"=="10" goto view_budget
if "%choice%"=="11" goto view_workflow_tasks
if "%choice%"=="12" goto export_structure
if "%choice%"=="13" goto mysql_cli
if "%choice%"=="14" goto end
goto menu

:show_all_tables
echo.
echo === 所有表及记录数统计 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT table_name AS '表名', table_rows AS '记录数' FROM information_schema.tables WHERE table_schema = 'shuju' ORDER BY table_rows DESC;"
pause
goto menu

:describe_table
echo.
set /p table_name=请输入表名: 
echo.
echo === %table_name% 表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE %table_name%;"
echo.
echo === %table_name% 建表语句 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SHOW CREATE TABLE %table_name%\G"
pause
goto menu

:view_all_data
echo.
set /p table_name=请输入表名: 
set /p limit_num=显示前多少条记录 (默认20): 
if "%limit_num%"=="" set limit_num=20
echo.
echo === %table_name% 表数据（前%limit_num%条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT * FROM %table_name% LIMIT %limit_num%;"
pause
goto menu

:view_internal_control
echo.
echo === 内控预警表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE internal_control_log;"
echo.
echo === 内控预警数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT log_id AS '日志ID', alert_type AS '预警类型', related_doc_no AS '相关单据', alert_time AS '预警时间', LEFT(alert_content, 60) AS '预警内容', handle_status AS '处理状态', handler AS '处理人' FROM internal_control_log ORDER BY alert_time DESC;"
pause
goto menu

:view_users
echo.
echo === 用户表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE user;"
echo.
echo === 用户数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT id AS 'ID', username AS '用户名', real_name AS '真实姓名', email AS '邮箱', phone AS '电话', department AS '部门', role AS '角色', status AS '状态' FROM user;"
pause
goto menu

:view_purchase_orders
echo.
echo === 采购订单表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE purchase_order;"
echo.
echo === 采购订单数据（最近10条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT order_no AS '订单号', supplier_name AS '供应商', total_amount AS '总金额', status AS '状态', create_time AS '创建时间' FROM purchase_order ORDER BY create_time DESC LIMIT 10;"
pause
goto menu

:view_production_orders
echo.
echo === 生产订单表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE production_order;"
echo.
echo === 生产订单数据（最近10条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT order_no AS '订单号', product_name AS '产品名称', quantity AS '数量', status AS '状态', create_time AS '创建时间' FROM production_order ORDER BY create_time DESC LIMIT 10;"
pause
goto menu

:view_suppliers
echo.
echo === 供应商表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE supplier;"
echo.
echo === 供应商数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT supplier_id AS 'ID', supplier_name AS '名称', contact_person AS '联系人', phone AS '电话', address AS '地址', status AS '状态' FROM supplier;"
pause
goto menu

:view_products
echo.
echo === 产品表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE product;"
echo.
echo === 产品数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT product_id AS 'ID', product_name AS '名称', product_code AS '编码', unit_price AS '单价', stock_quantity AS '库存', status AS '状态' FROM product;"
pause
goto menu

:view_budget
echo.
echo === 预算执行表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE budget_execution;"
echo.
echo === 预算执行数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT budget_code AS '编码', budget_name AS '名称', budget_amount AS '预算金额', used_amount AS '已用金额', remaining_amount AS '剩余金额' FROM budget_execution LIMIT 10;"
pause
goto menu

:view_workflow_tasks
echo.
echo === 工作流任务表结构 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "DESCRIBE workflow_task;"
echo.
echo === 工作流任务数据（最近10条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT task_id AS '任务ID', task_name AS '任务名', assignee_name AS '处理人', status AS '状态', create_time AS '创建时间' FROM workflow_task ORDER BY create_time DESC LIMIT 10;"
pause
goto menu

:export_structure
echo.
echo 正在导出所有表结构...
set export_file=table_structure_%date:~0,4%%date:~5,2%%date:~8,2%.txt
docker exec mysql80 mysqldump -uroot -p123456 shuju --no-data --default-character-set=utf8mb4 > "%export_file%"
echo.
echo 表结构已导出到: %export_file%
pause
goto menu

:mysql_cli
echo.
echo 进入 MySQL 命令行界面...
echo 常用命令：
echo   SHOW TABLES;              - 查看所有表
echo   DESCRIBE 表名;            - 查看表结构
echo   SELECT * FROM 表名 LIMIT 10;  - 查看数据
echo   exit 或 quit              - 退出
echo.
docker exec -it mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4
goto menu

:end
echo.
echo 感谢使用！
exit
