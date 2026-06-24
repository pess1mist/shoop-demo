@echo off
chcp 65001 >nul
echo ========================================
echo   MySQL 数据库查看工具
echo ========================================
echo.

:menu
echo 请选择操作：
echo 1. 查看所有表
echo 2. 查看指定表的数据（前10条）
echo 3. 查看内控预警数据
echo 4. 查看用户表
echo 5. 查看采购订单表
echo 6. 查看生产订单表
echo 7. 进入 MySQL 命令行
echo 8. 退出
echo.
set /p choice=请输入选项 (1-8): 

if "%choice%"=="1" goto show_tables
if "%choice%"=="2" goto view_table
if "%choice%"=="3" goto view_internal_control
if "%choice%"=="4" goto view_users
if "%choice%"=="5" goto view_purchase_orders
if "%choice%"=="6" goto view_production_orders
if "%choice%"=="7" goto mysql_cli
if "%choice%"=="8" goto end
goto menu

:show_tables
echo.
echo === 所有表列表 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SHOW TABLES;"
pause
goto menu

:view_table
echo.
set /p table_name=请输入表名: 
echo.
echo === %table_name% 表的前10条数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT * FROM %table_name% LIMIT 10;"
pause
goto menu

:view_internal_control
echo.
echo === 内控预警数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT log_id, alert_type, LEFT(alert_content, 50) as content, handle_status FROM internal_control_log ORDER BY alert_time DESC LIMIT 10;"
pause
goto menu

:view_users
echo.
echo === 用户表数据 ===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT user_id, username, real_name, role FROM user;"
pause
goto menu

:view_purchase_orders
echo.
echo === 采购订单表（前10条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT order_no, supplier_name, total_amount, status FROM purchase_order ORDER BY create_time DESC LIMIT 10;"
pause
goto menu

:view_production_orders
echo.
echo === 生产订单表（前10条）===
docker exec mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4 -e "SELECT order_no, product_name, quantity, status FROM production_order ORDER BY create_time DESC LIMIT 10;"
pause
goto menu

:mysql_cli
echo.
echo 进入 MySQL 命令行界面...
echo 输入 'exit' 或 'quit' 退出
echo.
docker exec -it mysql80 mysql -uroot -p123456 shuju --default-character-set=utf8mb4
goto menu

:end
echo.
echo 感谢使用！
exit
