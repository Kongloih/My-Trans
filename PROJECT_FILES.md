# 项目文件清单

## 核心文件

### 应用配置
- `pom.xml` - Maven项目配置文件
- `src/main/resources/application.properties` - Spring Boot应用配置
- `src/main/java/com/banking/TransactionManagementApplication.java` - 应用启动类

### Docker相关
- `Dockerfile` - Docker镜像构建文件（使用本地构建策略）
- `docker-compose.yml` - Docker Compose配置文件
- `.dockerignore` - Docker构建忽略文件

## 文档文件

### 主要文档
- `README.md` - 项目主要说明文档
- `DOCKER_BUILD_GUIDE.md` - Docker构建详细指南
- `TEST_REPORT.md` - 测试报告文档
- `PROJECT_FILES.md` - 本文件，项目文件清单

## 脚本文件

### 快速开始脚本
- `quick-start.sh` - Linux/Mac快速开始脚本
- `quick-start.bat` - Windows快速开始脚本

### Maven包装器
- `mvnw` - Linux/Mac Maven包装器
- `mvnw.cmd` - Windows Maven包装器
- `.mvn/wrapper/maven-wrapper.properties` - Maven包装器配置

## 源代码结构

### 主要包结构
```
src/main/java/com/banking/
├── config/          # 配置类
├── controller/      # REST控制器
├── dto/            # 数据传输对象
├── exception/      # 异常处理
├── model/          # 实体模型
├── repository/     # 数据访问层
├── service/        # 业务逻辑层
└── TransactionManagementApplication.java
```

### 测试代码
```
src/test/java/com/banking/
├── controller/     # 控制器测试
├── repository/     # 数据访问测试
├── service/        # 服务层测试
└── StressTest.java # 压力测试
```

## 构建输出

### 目标文件
- `target/transaction-management-1.0.0.jar` - 构建的JAR文件
- `target/classes/` - 编译后的类文件
- `target/test-classes/` - 测试类文件
- `target/site/` - 测试报告和文档

## 部署文件

### Docker镜像
- `transaction-management:latest` - Docker镜像名称

### 容器配置
- 端口映射: `8080:8080`
- 健康检查: `/actuator/health`
- 环境变量: `JAVA_OPTS`, `SPRING_PROFILES_ACTIVE`

## 访问地址

### 应用端点
- 应用主页: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/actuator/health
- 应用信息: http://localhost:8080/actuator/info

### 数据库
- H2控制台: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- 用户名: `sa`
- 密码: `password`

## 常用命令

### 开发命令
```bash
# 构建项目
mvn clean package -DskipTests

# 运行测试
mvn test

# 本地运行
mvn spring-boot:run

# 查看测试报告
mvn site
```

### Docker命令
```bash
# 构建镜像
docker build -t transaction-management .

# 运行容器
docker run -p 8080:8080 transaction-management

# 使用Docker Compose
docker-compose up -d

# 查看日志
docker logs -f transaction-management
```

### 快速开始
```bash
# Linux/Mac
./quick-start.sh

# Windows
quick-start.bat
```

## 注意事项

1. **构建顺序**: 必须先运行 `mvn clean package` 生成JAR文件，再构建Docker镜像
2. **端口冲突**: 确保8080端口未被占用
3. **内存配置**: 根据服务器配置调整JVM参数
4. **数据持久化**: 生产环境建议配置外部数据库
5. **安全配置**: 生产环境需要配置适当的安全措施

## 版本信息

- Java版本: 21
- Spring Boot版本: 3.2.0
- Maven版本: 3.6+
- Docker版本: 20.10+

## 支持

如有问题，请参考：
1. [README.md](README.md) - 项目概述和快速开始
2. [DOCKER_BUILD_GUIDE.md](DOCKER_BUILD_GUIDE.md) - Docker部署详细说明
3. [TEST_REPORT.md](TEST_REPORT.md) - 测试报告和问题排查 