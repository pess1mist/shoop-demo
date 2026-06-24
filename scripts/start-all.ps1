# 一键启动前后端服务脚本 (start-all.ps1)
Write-Host "========================================" -ForegroundColor Green
Write-Host "   业财一体化管理系统 - 一键启动脚本" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 设置错误处理
$ErrorActionPreference = "Continue"

# 1. 启动 MySQL 服务
Write-Host "`n[1/3] 正在启动 MySQL 服务..." -ForegroundColor Yellow
try {
    # 尝试通过 Windows 服务启动 MySQL
    $mysqlService = Get-Service -Name "MySQL80" -ErrorAction SilentlyContinue
    if ($mysqlService) {
        if ($mysqlService.Status -ne "Running") {
            Start-Service -Name "MySQL80"
            Write-Host "✓ MySQL 服务已启动" -ForegroundColor Green
        } else {
            Write-Host "✓ MySQL 服务已在运行" -ForegroundColor Green
        }
    } else {
        Write-Host "! 未找到 MySQL80 服务，尝试直接启动 mysqld..." -ForegroundColor Cyan
        # 尝试常见的 MySQL 安装路径
        $mysqlPaths = @(
            "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe",
            "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysqld.exe",
            "C:\xampp\mysql\bin\mysqld.exe"
        )
        
        $started = $false
        foreach ($path in $mysqlPaths) {
            if (Test-Path $path) {
                Start-Process -FilePath $path -ArgumentList "--console" -WindowStyle Hidden
                Write-Host "✓ MySQL 已从 $path 启动" -ForegroundColor Green
                $started = $true
                break
            }
        }
        
        if (-not $started) {
            Write-Host "✗ 未找到 MySQL 可执行文件，请手动启动 MySQL" -ForegroundColor Red
        }
    }
    
    # 等待 MySQL 完全启动
    Write-Host "等待 MySQL 完全启动..." -ForegroundColor Gray
    Start-Sleep -Seconds 5
    
} catch {
    Write-Host "✗ MySQL 启动失败: $_" -ForegroundColor Red
}

# 2. 启动后端服务
Write-Host "`n[2/3] 正在启动后端服务..." -ForegroundColor Yellow
$backendPath = Join-Path $PSScriptRoot "..\backend"
if (Test-Path $backendPath) {
    Set-Location $backendPath
    
    # 检查 Maven 是否可用
    $mavenExists = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mavenExists) {
        Write-Host "使用 Maven 启动后端..." -ForegroundColor Gray
        
        # 在新窗口中启动后端
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; mvn spring-boot:run" -WindowStyle Normal
        
        Write-Host "✓ 后端服务正在启动 (端口 8080)" -ForegroundColor Green
    } else {
        Write-Host "✗ 未找到 Maven，请确保已安装并配置环境变量" -ForegroundColor Red
    }
} else {
    Write-Host "✗ 后端目录不存在: $backendPath" -ForegroundColor Red
}

# 等待后端启动
Write-Host "等待后端服务启动..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# 3. 启动前端服务
Write-Host "`n[3/3] 正在启动前端服务..." -ForegroundColor Yellow
$frontendPath = Join-Path $PSScriptRoot "..\frontend"
if (Test-Path $frontendPath) {
    Set-Location $frontendPath
    
    # 检查 Node.js 是否可用
    $nodeExists = Get-Command node -ErrorAction SilentlyContinue
    if ($nodeExists) {
        Write-Host "使用 Vite 启动前端..." -ForegroundColor Gray
        
        # 在新窗口中启动前端
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; npm run dev" -WindowStyle Normal
        
        Write-Host "✓ 前端服务正在启动 (通常端口 5173)" -ForegroundColor Green
    } else {
        Write-Host "✗ 未找到 Node.js，请确保已安装并配置环境变量" -ForegroundColor Red
    }
} else {
    Write-Host "✗ 前端目录不存在: $frontendPath" -ForegroundColor Red
}

# 返回原始目录
Set-Location $PSScriptRoot

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "   启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "后端地址: http://localhost:8080" -ForegroundColor Cyan
Write-Host "前端地址: http://localhost:5173 (或显示的其他端口)" -ForegroundColor Cyan
Write-Host "`n提示: 两个新窗口已打开，分别运行后端和前端服务" -ForegroundColor Yellow
Write-Host "按任意键关闭此窗口..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
