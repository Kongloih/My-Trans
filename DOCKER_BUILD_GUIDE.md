# Docker构建指南

## 项目概述
这是一个基于Spring Boot的银行交易管理系统，使用Java 21和Maven构建。

## 构建步骤

### 1. 本地构建项目
首先在本地构建项目以生成JAR文件：
```bash
mvn clean package -DskipTests
```

### 2. 构建Docker镜像
使用本地构建的JAR文件创建Docker镜像：
```bash
docker build --no-cache -t transaction-management .
```

### 3. 运行容器
启动Docker容器：
```bash
docker run -p 8080:8080 transaction-management
```

## 访问应用

构建并运行成功后，可以通过以下地址访问应用：

- **应用主页**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info

## Dockerfile说明

当前使用的Dockerfile采用以下策略：

1. **使用本地构建**: 避免在容器内进行Maven构建，减少网络依赖
2. **多阶段构建**: 使用`mcr.microsoft.com/openjdk/jdk:21-ubuntu`作为基础镜像
3. **安全配置**: 创建非root用户运行应用
4. **健康检查**: 配置应用健康检查端点
5. **JVM优化**: 设置合适的内存和GC参数

## 故障排除

### 常见问题

1. **构建失败 - 找不到JAR文件**
   - 确保先运行`mvn clean package -DskipTests`生成JAR文件
   - 检查target目录下是否有`transaction-management-*.jar`文件

2. **端口冲突**
   - 如果8080端口被占用，可以使用其他端口：
   ```bash
   docker run -p 8081:8080 transaction-management
   ```

3. **内存不足**
   - 调整JVM参数：
   ```bash
   docker run -p 8080:8080 -e JAVA_OPTS="-Xmx1g -Xms512m" transaction-management
   ```

4. **健康检查失败**
   - 检查应用是否正常启动
   - 查看容器日志：`docker logs <container_id>`

### 查看容器状态
```bash
# 查看运行中的容器
docker ps

# 查看容器日志
docker logs <container_id>

# 进入容器
docker exec -it <container_id> /bin/bash
```

## 开发环境

### 系统要求
- Java 21
- Maven 3.6+
- Docker Desktop

### 本地开发
```bash
# 克隆项目
git clone <repository-url>
cd backend

# 安装依赖
mvn dependency:resolve

# 运行测试
mvn test

# 本地运行
mvn spring-boot:run
```

## 生产部署

### 使用Docker Compose
项目包含`docker-compose.yml`文件，可以一键启动：
```bash
docker-compose up -d
```

### 环境变量配置
可以通过环境变量配置应用：
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  transaction-management
```

## 性能优化

### JVM参数调优
根据服务器配置调整JVM参数：
```bash
# 小内存服务器 (1-2GB)
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# 中等内存服务器 (4-8GB)
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:+UseStringDeduplication"

# 大内存服务器 (16GB+)
JAVA_OPTS="-Xmx8g -Xms4g -XX:+UseG1GC -XX:+UseStringDeduplication -XX:+UseCompressedOops"
```

### Docker资源限制
```bash
docker run -p 8080:8080 \
  --memory=1g \
  --cpus=1.0 \
  transaction-management
```

## 监控和维护

### 应用监控
- 健康检查: `/actuator/health`
- 应用信息: `/actuator/info`
- 指标监控: `/actuator/metrics`

### 日志管理
```bash
# 查看实时日志
docker logs -f <container_id>

# 导出日志
docker logs <container_id> > app.log
```

## 联系支持

如果遇到问题，请检查：
1. Docker版本是否最新
2. 网络连接是否正常
3. 系统资源是否充足
4. 防火墙设置是否正确 