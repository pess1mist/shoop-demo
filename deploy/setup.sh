#!/bin/bash
# =============================================
# 业财一体化管理系统 - 阿里云一键部署脚本
# =============================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR="/opt/finance"
LOG_DIR="/var/log/finance"
DB_NAME="shuju"
DB_USER="finance"
DB_PASS="Finance@2024!Demo"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

log()  { echo -e "${GREEN}[OK]${NC} $1"; }
err()  { echo -e "${RED}[ERR]${NC} $1"; exit 1; }

echo "=========================================="
echo "  业财一体化管理系统 部署开始"
echo "  $(date)"
echo "=========================================="

# ---- 1. 检测系统 ----
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
else
    err "无法检测操作系统"
fi
log "检测到系统: $OS"

# ---- 2. 安装 Java 17 ----
if ! java -version 2>&1 | grep -q "17"; then
    log "正在安装 Java 17..."
    case $OS in
        centos|rhel|alinux|anolis)
            yum install -y java-17-openjdk java-17-openjdk-devel 2>&1 | tail -1
            ;;
        ubuntu|debian)
            apt-get update -qq && apt-get install -y -qq openjdk-17-jdk 2>&1 | tail -1
            ;;
        *) err "不支持的系统: $OS" ;;
    esac
    log "Java 17 安装完成"
else
    log "Java 17 已安装"
fi

# ---- 3. 安装 MySQL 8.0 ----
if ! mysql --version 2>&1 | grep -q "8"; then
    log "正在安装 MySQL 8.0..."
    case $OS in
        centos|rhel|alinux|anolis)
            yum install -y mysql-server 2>&1 | tail -1
            ;;
        ubuntu|debian)
            apt-get install -y -qq mysql-server 2>&1 | tail -1
            ;;
    esac
    log "MySQL 安装完成"
else
    log "MySQL 已安装"
fi

# 启动 MySQL
systemctl start mysqld 2>/dev/null || systemctl start mysql 2>/dev/null
systemctl enable mysqld 2>/dev/null || systemctl enable mysql 2>/dev/null

# ---- 4. 配置 MySQL ----
log "配置 MySQL 数据库和用户..."
# 创建数据库
mysql -u root <<SQL 2>/dev/null || true
CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
SQL
log "数据库 shuju 和用户 finance 创建完成"

# ---- 5. 导入数据 ----
log "导入初始数据..."
mysql -u ${DB_USER} -p${DB_PASS} ${DB_NAME} < ${SCRIPT_DIR}/init_db.sql 2>&1
log "数据导入完成"

# ---- 6. 安装 Nginx ----
if ! nginx -v 2>&1 | grep -q "nginx"; then
    log "正在安装 Nginx..."
    case $OS in
        centos|rhel|alinux|anolis)
            yum install -y nginx 2>&1 | tail -1
            ;;
        ubuntu|debian)
            apt-get install -y -qq nginx 2>&1 | tail -1
            ;;
    esac
    log "Nginx 安装完成"
else
    log "Nginx 已安装"
fi

# ---- 7. 创建应用目录 ----
mkdir -p ${APP_DIR} ${LOG_DIR}

# ---- 8. 部署后端 ----
log "部署后端..."
cp ${SCRIPT_DIR}/finance-management-1.0.0.jar ${APP_DIR}/
cp ${SCRIPT_DIR}/application-prod.yml ${APP_DIR}/application.yml

# 创建 systemd 服务
cat > /etc/systemd/system/finance.service << EOF
[Unit]
Description=Finance Management System
After=network.target mysql.service mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=${APP_DIR}
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m ${APP_DIR}/finance-management-1.0.0.jar --spring.config.location=${APP_DIR}/application.yml
Restart=on-failure
RestartSec=10
StandardOutput=append:${LOG_DIR}/finance.log
StandardError=append:${LOG_DIR}/finance-error.log

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable finance
log "后端服务配置完成"

# ---- 9. 部署前端 + Nginx ----
log "部署前端..."
mkdir -p /usr/share/nginx/html
rm -rf /usr/share/nginx/html/*
cp -r ${SCRIPT_DIR}/dist/* /usr/share/nginx/html/

cat > /etc/nginx/conf.d/finance.conf << 'NGINX'
server {
    listen       80;
    server_name  _;

    # 前端静态文件
    location / {
        root   /usr/share/nginx/html;
        index  index.html;
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理到后端
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 300s;
        proxy_connect_timeout 10s;
    }
}
NGINX

# 删除默认配置
rm -f /etc/nginx/conf.d/default.conf /etc/nginx/sites-enabled/default 2>/dev/null || true
log "Nginx 配置完成"

# ---- 10. 配置防火墙 ----
log "配置防火墙..."
# 阿里云安全组需在控制台开放 80 端口！
if command -v firewall-cmd &>/dev/null && systemctl is-active firewalld &>/dev/null; then
    firewall-cmd --permanent --add-port=80/tcp 2>/dev/null
    firewall-cmd --permanent --add-port=8080/tcp 2>/dev/null
    firewall-cmd --reload 2>/dev/null
    log "firewalld 端口已开放"
elif command -v ufw &>/dev/null; then
    ufw allow 80/tcp 2>/dev/null
    ufw allow 8080/tcp 2>/dev/null
    log "ufw 端口已开放"
fi

# ---- 11. 启动服务 ----
log "启动服务..."
systemctl restart nginx
systemctl restart finance
log "Nginx 已启动 (端口 80)"
log "后端服务已启动 (端口 8080)"

# ---- 12. 验证 ----
sleep 3
echo ""
echo "=========================================="
echo "  部署完成！验证结果："
echo "=========================================="
echo ""

# 检查服务状态
if systemctl is-active nginx &>/dev/null; then
    log "Nginx:  运行中 ✓"
else
    err "Nginx:  启动失败!"
fi

if systemctl is-active finance &>/dev/null; then
    log "后端:   运行中 ✓"
else
    log "后端正在启动中，请稍等..."
    sleep 10
    if systemctl is-active finance &>/dev/null; then
        log "后端:   运行中 ✓"
    else
        log "后端可能还在启动，查看日志: journalctl -u finance -f"
    fi
fi

# MySQL 检查
if mysql -u ${DB_USER} -p${DB_PASS} -e "SELECT 1" ${DB_NAME} &>/dev/null; then
    log "MySQL:  连接正常 ✓"
else
    log "MySQL:  请检查配置"
fi

echo ""
echo "=========================================="
echo "  访问地址: http://$(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')"
echo "  后端日志: journalctl -u finance -f"
echo "  Nginx日志: /var/log/nginx/access.log"
echo "=========================================="
