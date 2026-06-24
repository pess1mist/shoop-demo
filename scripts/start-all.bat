@echo off
chcp 65001 >nul
title 业财一体化管理系统 - 一键启动

echo ========================================
echo    业财一体化管理系统 - 一键启动脚本
echo ========================================

:: 1. 启动 MySQL 服务
echo.
echo [1/3] 正在启动 MySQL 服务...
net start MySQL80 2>nul
if %errorlevel% neq 0 (
    echo ! 未找到 MySQL80 服务，尝试直接启动 mysqld...
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" (
        start "" "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --console
        echo ✓ MySQL 已尝试启动
    ) else (
        echo ✗ 未找到 MySQL 可执行文件，请手动启动 MySQL
    )
) else (
    echo ✓ MySQL 服务已启动或在运行
)

:: 等待 MySQL 完全启动
timeout /t 5 /nobreak >nul

:: 2. 启动后端服务
echo.
echo [2/3] 正在启动后端服务...
cd ..\backend
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    start "后端服务" cmd /k "mvn spring-boot:run"
    echo ✓ 后端服务正在启动 (端口 8080)
) else (
    echo ✗ 未找到 Maven，请确保已安装并配置环境变量
)

:: 等待后端启动
timeout /t 10 /nobreak >nul

:: 3. 启动前端服务
echo.
echo [3/3] 正在启动前端服务...
cd ..\frontend
where node >nul 2>&1
if %errorlevel% equ 0 (
    start "前端服务" cmd /k "npm run dev"
    echo ✓ 前端服务正在启动 (通常端口 5173)
) else (
    echo ✗ 未找到 Node.js，请确保已安装并配置环境变量
)

:: 返回原始目录
cd ..\scripts

echo.
echo ========================================
echo    启动完成！
echo ========================================
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:5173 (或显示的其他端口)
echo.
echo 提示: 两个新窗口已打开，分别运行后端和前端服务
pause
