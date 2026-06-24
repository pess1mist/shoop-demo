docker pull docker.xuanyuan.me/library/mysql:8.0@echo off
chcp 65001 >nul
echo ========================================
echo   Use Docker to Start MySQL 8.0
echo ========================================
echo.

:: Check if running as administrator
net session >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Please run this script as Administrator!
    echo.
    echo Solution:
    echo 1. Right-click on this file
    echo 2. Select "Run as administrator"
    echo.
    pause
    exit /b 1
)

:: Check Docker status
echo [INFO] Checking Docker status...
docker ps >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [INFO] Checking MySQL container status...

:: Check if container exists
docker ps -a --format "{{.Names}}" | findstr /c:"mysql80" >nul 2>&1
if not errorlevel 1 (
    echo [WARNING] Found existing mysql80 container
    set /p choice="Do you want to delete the old container and recreate? (Y/N): "
    if /i "%choice%"=="Y" (
        echo [INFO] Stopping and removing old container...
        docker stop mysql80 >nul 2>&1
        docker rm -f mysql80
        echo [INFO] Old container removed
    ) else (
        echo [INFO] Attempting to start existing container...
        docker start mysql80
        goto :check_status
    )
) else (
    echo [INFO] No existing container found, will create new one
)

echo.
echo [INFO] Checking if port 3306 is in use...
netstat -ano | findstr ":3306" >nul 2>&1
if not errorlevel 1 (
    echo [WARNING] Port 3306 is already in use!
    echo.
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3306"') do (
        echo [INFO] Process PID using this port: %%a
        tasklist | findstr /c:"%%a"
    )
    echo.
    echo Solutions:
    echo 1. Close the program using port 3306
    echo 2. Or modify the script to use a different port (e.g., 3307)
    echo.
    set /p force="Force continue anyway? (Y/N): "
    if /i not "%force%"=="Y" (
        pause
        exit /b 1
    )
)

echo.
echo [INFO] Pulling MySQL 8.0 image...
echo [NOTE] This may take a few minutes on first run
echo.

:: Try multiple mirror sources
set MIRRORS=daocloud.io/library/mysql:8.0 docker.mirror.qq.com/library/mysql:8.0 mysql:8.0
set PULLED=0

for %%i in (%MIRRORS%) do (
    echo [TRY] Pulling from %%i...
    docker pull %%i 2>nul
    if not errorlevel 1 (
        set PULLED=1
        goto :run_container
    )
    echo [FAIL] Cannot pull from %%i, trying next mirror...
    echo.
)

if %PULLED%==0 (
    echo [ERROR] All mirror sources unreachable. Check network or configure Docker accelerator.
    echo.
    echo Recommended solution:
    echo 1. Open Docker Desktop - Settings - Docker Engine
    echo 2. Add registry-mirrors configuration:
    echo    "registry-mirrors": [
    echo      "https://docker.m.daocloud.io",
    echo      "https://docker.1panel.live"
    echo    ]
    echo 3. Restart Docker Desktop
    pause
    exit /b 1
)

:run_container
echo.
echo [INFO] Creating and starting MySQL container...
echo [NOTE] Data volume will be saved to: %~dp0mysql-data

:: Ensure data directory exists
if not exist "%~dp0mysql-data" (
    echo [INFO] Creating data directory...
    mkdir "%~dp0mysql-data"
)

docker run --name mysql80 ^
  -e MYSQL_ROOT_PASSWORD=123456 ^
  -p 3306:3306 ^
  -v "%~dp0mysql-data:/var/lib/mysql" ^
  -d mysql:8.0 ^
  --character-set-server=utf8mb4 ^
  --collation-server=utf8mb4_unicode_ci

if errorlevel 1 (
    echo [ERROR] Container creation failed. Details:
    docker run --name mysql80 ^
      -e MYSQL_ROOT_PASSWORD=123456 ^
      -p 3306:3306 ^
      -v "%~dp0mysql-data:/var/lib/mysql" ^
      -d mysql:8.0 ^
      --character-set-server=utf8mb4 ^
      --collation-server=utf8mb4_unicode_ci
    echo.
    echo Possible reasons:
    echo 1. Port 3306 is occupied
    echo 2. Insufficient permissions (run as administrator)
    echo 3. Docker service is not running properly
    pause
    exit /b 1
)

:check_status
echo.
echo [INFO] Waiting for MySQL to start...
timeout /t 5 /nobreak >nul

docker ps | findstr mysql80 >nul 2>&1
if not errorlevel 1 (
    echo.
    echo ========================================
    echo   MySQL 8.0 Started Successfully!
    echo ========================================
    echo.
    echo Connection Info:
    echo - Host: localhost
    echo - Port: 3306
    echo - Username: root
    echo - Password: 123456
    echo.
    echo Common Commands:
    echo - Enter MySQL: docker exec -it mysql80 mysql -uroot -p123456
    echo - View logs: docker logs mysql80
    echo - Stop: docker stop mysql80
    echo - Restart: docker restart mysql80
    echo.
    
    :: Test connection
    echo [INFO] Testing database connection...
    docker exec mysql80 mysql -uroot -p123456 -e "SELECT 'MySQL is running!' AS status;" >nul 2>&1
    if not errorlevel 1 (
        echo [SUCCESS] Database connection test passed
    ) else (
        echo [WARNING] Database connection test failed, please try again later
    )
    
    echo.
    echo Data files saved to: %~dp0mysql-data
    echo.
) else (
    echo [ERROR] Container startup failed. Check logs:
    docker logs mysql80
    echo.
    echo Common solutions:
    echo 1. Re-run this script as administrator
    echo 2. Check if Docker Desktop is running properly
    echo 3. Check Docker logs for detailed errors
)

pause