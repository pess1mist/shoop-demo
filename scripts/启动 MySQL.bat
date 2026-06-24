@echo off
echo 正在启动 MySQL 服务...
net start MySQL80
if %errorlevel% neq 0 (
    echo MySQL 服务启动失败，尝试直接启动 mysqld...
    start "" "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe"
    timeout /t 5 /nobreak
    echo MySQL 已尝试启动，请检查是否运行
) else (
    echo MySQL 服务启动成功！
)
pause
