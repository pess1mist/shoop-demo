# 🐳 Docker 运行 MySQL 8.0 完整指南

## 方法一：使用国内镜像源（推荐）

### 1. 配置 Docker 镜像加速器

编辑 Docker Desktop 设置：
1. 打开 Docker Desktop
2. 点击 Settings → Docker Engine
3. 添加以下镜像加速器配置：

```json
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1panel.live",
    "https://hub.rat.dev",
    "https://dhub.kubesre.xyz"
  ]
}
```

4. 点击 Apply & Restart 重启 Docker

### 2. 拉取 MySQL 镜像

```bash
# 使用官方镜像（配置加速器后）
docker pull mysql:8.0

# 或使用 DaoCloud 镜像
docker pull daocloud.io/library/mysql:8.0
```

### 3. 启动 MySQL 容器

```bash
docker run --name mysql80 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -p 3306:3306 \
  -v C:/Users/19012/Desktop/1/mysql-data:/var/lib/mysql \
  -d mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

**参数说明：**
- `--name mysql80`: 容器名称
- `-e MYSQL_ROOT_PASSWORD=123456`: 设置 root 密码
- `-p 3306:3306`: 映射端口到主机
- `-v`: 数据卷挂载（持久化数据）
- `-d`: 后台运行
- `--character-set-server=utf8mb4`: 设置字符集

## 方法二：使用已有 MySQL 安装

如果您已经有 MySQL 8.0 安装在本地，可以直接使用 Windows 服务启动：

### 以管理员身份运行 PowerShell

```powershell
# 方法 1: 使用 Start-Service
Start-Service MySQL80

# 方法 2: 使用 net 命令
net start MySQL80

# 方法 3: 直接运行 mysqld
mysqld --console
```

### 如果服务不存在，重新注册 MySQL 服务

```powershell
# 进入 MySQL 安装目录
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

# 移除旧服务（如果存在）
.\mysqld --remove MySQL80

# 初始化数据目录（首次安装需要）
.\mysqld --initialize-insecure

# 安装服务
.\mysqld --install MySQL80

# 启动服务
net start MySQL80
```

## 方法三：使用 Docker Compose（最方便）

创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: mysql80
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: "123456"
      MYSQL_DATABASE: "finance_db"
      MYSQL_USER: "finance_user"
      MYSQL_PASSWORD: "finance123"
    ports:
      - "3306:3306"
    volumes:
      - ./mysql-data:/var/lib/mysql
      - ./mysql-conf:/etc/mysql/conf.d
    command: 
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-authentication-plugin=mysql_native_password
```

然后执行：

```bash
# 启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down
```

## 验证 MySQL 运行

### 检查容器状态

```bash
docker ps | grep mysql80
```

### 进入 MySQL 容器

```bash
docker exec -it mysql80 mysql -uroot -p123456
```

### 测试连接

在 MySQL 命令行中执行：

```sql
SHOW DATABASES;
CREATE DATABASE finance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

## 连接到项目

修改项目的 `application.yml` 配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/finance_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 常用 Docker 命令

```bash
# 查看容器状态
docker ps -a

# 查看日志
docker logs mysql80

# 停止容器
docker stop mysql80

# 启动已停止的容器
docker start mysql80

# 重启容器
docker restart mysql80

# 删除容器（谨慎使用！）
docker rm -f mysql80

# 查看磁盘使用
docker system df

# 清理未使用的资源
docker system prune
```

## 数据备份与恢复

### 备份数据库

```bash
docker exec mysql80 mysqldump -uroot -p123456 finance_db > backup.sql
```

### 恢复数据库

```bash
docker exec -i mysql80 mysql -uroot -p123456 finance_db < backup.sql
```

## 故障排查

### 容器启动失败

```bash
# 查看详细日志
docker logs mysql80

# 进入容器调试
docker exec -it mysql80 bash
```

### 端口被占用

如果 3306 端口被占用，可以映射到其他端口：

```bash
docker run --name mysql80 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -p 3307:3306 \
  -d mysql:8.0
```

然后在项目中修改端口为 3307。

## 快速启动脚本

创建 `start-mysql-docker.bat` 文件：

```batch
@echo off
echo Starting MySQL 8.0 with Docker...
docker run --name mysql80 -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -d mysql:8.0
echo MySQL started successfully!
echo Connect with: docker exec -it mysql80 mysql -uroot -p123456
pause
```

双击运行即可启动 MySQL。

---

**推荐配置：**
- ✅ 使用方法三的 Docker Compose 方式（配置清晰、易于管理）
- ✅ 配置 Docker 镜像加速器避免拉取失败
- ✅ 使用数据卷挂载保证数据持久化
- ✅ 设置正确的字符集避免中文乱码

**注意事项：**
- ⚠️ 删除容器前务必备份数据
- ⚠️ 确保 Docker Desktop 正在运行
- ⚠️ 首次启动可能需要几分钟下载镜像
- ⚠️ 如果网络问题严重，考虑离线导入镜像
