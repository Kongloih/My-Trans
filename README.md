# 银行交易管理系统

一个基于Spring Boot的简单银行交易管理系统，支持交易的创建、查询、更新和删除操作。

## 功能特性

- ✅ **交易管理**: 创建、查询、更新、删除交易
- ✅ **账户余额跟踪**: 实时计算和显示账户余额
- ✅ **数据验证**: 完整的输入验证和错误处理
- ✅ **数据持久化**: H2数据库 + JPA + Caffeine缓存
- ✅ **Mock数据**: 自动生成测试数据和多场景测试用例
- ✅ **RESTful API**: 符合REST设计原则的API接口
- ✅ **分页查询**: 支持分页和条件查询
- ✅ **API文档**: 集成Swagger UI文档
- ✅ **监控指标**: Spring Boot Actuator监控
- ✅ **容器化**: Docker支持
- ✅ **全面测试**: 单元测试和集成测试

## 技术栈

### 核心框架
- **Java 21**: 使用最新的Java LTS版本
- **Spring Boot 3.2.0**: 企业级应用框架
- **Spring Web**: REST API开发
- **Spring Cache**: 缓存抽象层

### 数据持久化
- **H2 Database**: 内存/文件数据库，支持开发和测试
- **Spring Data JPA**: 数据访问抽象层
- **Hibernate**: ORM框架
- **HikariCP**: 高性能数据库连接池

### 性能优化
- **Caffeine Cache**: 高性能本地缓存
- **JPA二级缓存**: 数据库查询缓存
- **Stream API**: 高效的数据处理

### 验证和文档
- **Jakarta Validation**: Bean验证
- **SpringDoc OpenAPI**: API文档生成
- **Spring Boot Actuator**: 监控和指标

### 测试框架
- **JUnit 5**: 单元测试框架
- **Mockito**: Mock框架
- **Spring Boot Test**: 集成测试

### 工具和部署
- **Maven**: 项目构建管理
- **Docker**: 容器化部署
- **Jackson**: JSON序列化

## 快速开始

### 系统要求
- Java 21+
- Maven 3.6+
- Docker (可选)

### 本地运行

1. **克隆项目**
```bash
git clone <repository-url>
cd transaction-management
```

2. **编译项目**
```bash
mvn clean compile
```

3. **运行测试**
```bash
mvn test
```

4. **启动应用**
```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

### Docker运行

1. **构建Docker镜像**
```bash
docker build -t banking-transaction-system .
```

2. **运行容器**
```bash
docker run -p 8080:8080 banking-transaction-system
```

## 数据库配置

### H2数据库
系统使用H2内存数据库进行开发和测试，提供以下特性：

- **自动Schema创建**: 应用启动时自动创建数据表
- **Mock数据生成**: 自动生成测试数据（10个账户，150-250笔交易）
- **Web控制台**: 可视化数据库管理界面
- **快速重启**: 内存模式，重启时数据重新初始化

### 数据库访问

**H2控制台**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **用户名**: `sa`
- **密码**: `password`

### 数据配置
```properties
# 开发环境配置
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# 自动Mock数据
spring.sql.init.mode=always
```

### Mock测试数据
系统启动时自动生成：
- **10个测试账户**: 1234567890 ~ 0123456789
- **150-250笔交易**: 涵盖5种交易类型
- **90天历史数据**: 模拟真实交易场景
- **多种金额场景**: 0.01元 - 99,999.99元

## API文档

启动应用后，可以通过以下地址访问API文档：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 主要API接口

### 交易管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/transactions` | 创建新交易 |
| GET | `/api/transactions/{id}` | 获取交易详情 |
| PUT | `/api/transactions/{id}` | 更新交易 |
| DELETE | `/api/transactions/{id}` | 删除交易 |
| GET | `/api/transactions` | 分页获取交易列表 |

### 查询接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/transactions/account/{accountNumber}` | 按账户查询交易 |
| GET | `/api/transactions/type/{type}` | 按类型查询交易 |
| GET | `/api/transactions/balance/{accountNumber}` | 获取账户余额 |
| GET | `/api/transactions/statistics` | 获取统计信息 |

### 请求示例

**创建交易**
```json
POST /api/transactions
{
    "accountNumber": "1234567890",
    "amount": 1000.00,
    "type": "DEPOSIT",
    "description": "工资存款",
    "currency": "CNY"
}
```

**响应示例**
```json
{
    "success": true,
    "message": "交易创建成功",
    "data": {
        "id": 1,
        "accountNumber": "1234567890",
        "amount": 1000.00,
        "type": "DEPOSIT",
        "description": "工资存款",
        "timestamp": "2024-01-01 10:00:00",
        "currency": "CNY",
        "balance": 1000.00
    }
}
```

## 交易类型

系统支持以下交易类型：

- **DEPOSIT** (存款): 增加账户余额
- **WITHDRAWAL** (取款): 减少账户余额
- **TRANSFER** (转账): 减少账户余额
- **PAYMENT** (支付): 减少账户余额
- **REFUND** (退款): 增加账户余额

## 监控和健康检查

### Actuator端点

- **健康检查**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info
- **指标数据**: http://localhost:8080/actuator/metrics
- **缓存信息**: http://localhost:8080/actuator/cache
- **数据源信息**: http://localhost:8080/actuator/health/db

### 系统访问地址

- **应用主页**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **H2数据库控制台**: http://localhost:8080/h2-console
- **健康检查**: http://localhost:8080/actuator/health

## 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn integration-test
```

### 测试覆盖率
```bash
mvn jacoco:report
```

### 数据库测试
```bash
# 运行数据库集成测试
mvn test -Dtest=TransactionRepositoryIntegrationTest

# 运行JPA Repository测试
mvn test -Dtest=*Repository*
```

### 压力测试

系统包含压力测试脚本，可以测试高并发场景下的性能：

```bash
# 运行压力测试（需要应用正在运行）
mvn test -Dtest=StressTest
```

### 测试数据管理
```bash
# 查看H2数据库中的测试数据
# 1. 启动应用: mvn spring-boot:run
# 2. 访问: http://localhost:8080/h2-console
# 3. 使用JDBC URL: jdbc:h2:mem:testdb
```

## 性能特性

### 缓存策略
- **Caffeine缓存**: 30分钟写入过期，10分钟访问过期
- **最大缓存条目**: 1000个
- **缓存统计**: 启用性能监控

### 并发性能
- **ConcurrentHashMap**: 线程安全的内存存储
- **原子操作**: 使用AtomicLong生成ID
- **读写分离**: 优化查询性能

### 内存优化
- **流式处理**: 使用Stream API处理大量数据
- **分页查询**: 支持大数据集的分页访问
- **压缩响应**: 启用HTTP压缩

## 错误处理

系统提供完整的错误处理机制：

### 错误类型
- **TRANSACTION_NOT_FOUND**: 交易不存在
- **VALIDATION_ERROR**: 输入验证失败
- **INVALID_ARGUMENT**: 参数错误
- **INSUFFICIENT_BALANCE**: 余额不足
- **INTERNAL_ERROR**: 系统内部错误

### 错误响应格式
```json
{
    "success": false,
    "errorCode": "TRANSACTION_NOT_FOUND",
    "message": "交易ID 123 不存在",
    "timestamp": "2024-01-01 10:00:00",
    "status": 404
}
```

## 开发指南

### 项目结构
```
src/
├── main/java/com/banking/
│   ├── controller/          # REST控制器
│   ├── service/            # 业务逻辑服务
│   ├── repository/         # 数据访问层
│   ├── model/              # 实体模型
│   ├── dto/                # 数据传输对象
│   ├── exception/          # 异常处理
│   └── config/             # 配置类
├── main/resources/
│   └── application.properties  # 应用配置
└── test/java/              # 测试代码
```

### 编码规范
- 使用中文注释和错误消息
- 遵循RESTful API设计原则
- 完整的单元测试覆盖
- 详细的API文档

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- **项目维护者**: Banking System Team
- **邮箱**: banking@example.com
- **项目地址**: https://github.com/banking/transaction-management

## 更新日志

### v1.0.0 (2024-01-01)
- ✅ 初始版本发布
- ✅ 基础交易管理功能
- ✅ REST API接口
- ✅ 缓存和性能优化
- ✅ 完整的测试覆盖
- ✅ Docker容器化支持
- ✅ API文档和监控 