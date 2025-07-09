# 银行交易管理系统

一个基于Spring Boot的现代化银行交易管理系统，支持股票买卖交易的创建、查询、更新和删除操作，以及银行交易统计分析。

## 功能特性

- ✅ **银行交易管理**: 创建、查询、更新、删除银行交易记录
- ✅ **交易统计分析**: 按股票代码和账户统计当天交易数据
- ✅ **多字段支持**: 成交股数、成交价格、成交日期、股票代码等完整信息
- ✅ **数据验证**: 完整的输入验证和错误处理
- ✅ **数据持久化**: H2数据库 + JPA + Caffeine缓存
- ✅ **Mock数据**: 自动生成A股交易测试数据
- ✅ **RESTful API**: 符合REST设计原则的API接口
- ✅ **分页查询**: 支持分页和条件查询
- ✅ **API文档**: 集成Swagger UI文档
- ✅ **监控指标**: Spring Boot Actuator监控
- ✅ **容器化**: Docker支持
- ✅ **全面测试**: 单元测试、集成测试和压力测试

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

### 快速开始

#### 使用脚本（推荐）
- **Linux/Mac**: `./quick-start.sh`
- **Windows**: `quick-start.bat`

#### 手动运行

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

1. **本地构建项目**
```bash
mvn clean package -DskipTests
```

2. **构建Docker镜像**
```bash
docker build -t transaction-management .
```

3. **运行容器**
```bash
docker run -p 8080:8080 transaction-management
```

> **注意**: 在构建Docker镜像之前，需要先在本地构建项目生成JAR文件。详细说明请参考 [Docker构建指南](DOCKER_BUILD_GUIDE.md)。

## 数据库配置

### H2数据库
系统使用H2内存数据库进行开发和测试，提供以下特性：

- **自动Schema创建**: 应用启动时自动创建数据表
- **A股Mock数据**: 自动生成中国A股交易测试数据
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

# 禁用SQL脚本初始化，使用程序生成数据
spring.sql.init.mode=never
```

### A股Mock测试数据
系统启动时自动生成：
- **中国A股代码**: 000001(平安银行), 000002(万科A), 600036(招商银行), 600519(贵州茅台), 300015(爱尔眼科)
- **真实股票名称**: 对应实际A股上市公司
- **100-500笔交易**: 涵盖买入和卖出两种交易类型
- **当天交易数据**: 模拟实时交易场景
- **合理价格范围**: 10-100元/股价格区间

## API文档

启动应用后，可以通过以下地址访问API文档：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 主要API接口

### 银行交易管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/transactions` | 创建新的银行交易 |
| GET | `/api/transactions/{id}` | 获取交易详情 |
| PUT | `/api/transactions/{id}` | 更新交易信息 |
| DELETE | `/api/transactions/{id}` | 删除交易记录 |
| GET | `/api/transactions` | 分页获取交易列表 |

### 查询和统计接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/transactions/account/{accountNumber}` | 按账户查询交易 |
| GET | `/api/transactions/type/{type}` | 按类型查询交易(BUY/SELL) |
| GET | `/api/transactions/security/{securityCode}/account/{accountNumber}/statistics` | 获取银行交易统计 |
| GET | `/api/transactions/statistics` | 获取系统统计信息 |

## 数据结构

### 银行交易字段

| 字段名 | 数据库列名 | 类型 | 必填 | 描述 |
|--------|------------|------|------|------|
| id | id | Long | 是 | 交易唯一标识 |
| accountNumber | account_number | String | 是 | 16位账户号码 |
| amount | amount | BigDecimal | 是 | 交易总金额 |
| transType | trans_type | Enum | 是 | 交易类型(BUY/SELL) |
| unit | unit | Long | 是 | 成交股数 |
| price | price | BigDecimal | 是 | 成交价格(元/股) |
| transDate | trans_date | LocalDate | 是 | 成交日期 |
| securityCode | security_code | String | 是 | 股票代码 |
| description | description | String | 否 | 交易描述 |
| currency | currency | String | 否 | 货币类型(默认CNY) |
| timestamp | timestamp | LocalDateTime | 是 | 交易创建时间 |

### 请求示例

**创建股票买入交易**
```json
POST /api/transactions
{
    "accountNumber": "1234567890123456",
    "amount": 10000.00,
    "transType": "BUY",
    "unit": 1000,
    "price": 10.00,
    "transDate": "2024-01-01",
    "securityCode": "000001",
    "description": "买入平安银行",
    "currency": "CNY"
}
```

**创建股票卖出交易**
```json
POST /api/transactions
{
    "accountNumber": "1234567890123456",
    "amount": 12000.00,
    "transType": "SELL",
    "unit": 1000,
    "price": 12.00,
    "transDate": "2024-01-02",
    "securityCode": "000001",
    "description": "卖出平安银行",
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
        "accountNumber": "1234567890123456",
        "amount": 10000.00,
        "transType": "BUY",
        "unit": 1000,
        "price": 10.00,
        "transDate": "2024-01-01",
        "securityCode": "000001",
        "description": "买入平安银行",
        "currency": "CNY",
        "timestamp": "2024-01-01T10:00:00"
    }
}
```

## 交易类型

系统支持以下银行交易类型：

- **BUY** (买入): 股票买入交易
- **SELL** (卖出): 股票卖出交易

## 银行交易统计

系统提供详细的银行交易统计功能：

### 统计维度
- **按股票代码**: 统计特定股票的交易情况
- **按账户**: 统计特定账户的交易情况
- **按交易日期**: 统计当天的交易数据
- **按交易类型**: 分别统计买入和卖出

### 统计指标
- **买入总股数**: 当天买入的总股数
- **买入总金额**: 当天买入的总金额
- **买入均价**: 买入均价
- **卖出总股数**: 当天卖出的总股数
- **卖出总金额**: 当天卖出的总金额
- **卖出均价**: 卖出均价
- **净持仓变化**: 买入股数 - 卖出股数
- **交易笔数**: 总交易笔数

**统计查询示例**
```bash
GET /api/transactions/security/000001/account/1234567890123456/statistics
```

**统计响应示例**
```json
{
    "success": true,
    "message": "获取银行交易统计成功",
    "data": {
        "securityCode": "000001",
        "accountNumber": "1234567890123456",
        "transDate": "2024-01-01",
        "buyTotalUnit": 2000,
        "buyTotalAmount": 20000.00,
        "buyAveragePrice": 10.00,
        "sellTotalUnit": 500,
        "sellTotalAmount": 6000.00,
        "sellAveragePrice": 12.00,
        "netPositionChange": 1500,
        "totalTransactions": 3
    }
}
```

## 缓存策略

系统使用Caffeine缓存提高查询性能：

- **查询缓存**: 交易查询结果缓存
- **统计缓存**: 股票统计数据缓存
- **自动失效**: 数据更新时自动清除相关缓存

## 系统监控

- **健康检查**: `/actuator/health`
- **系统指标**: `/actuator/metrics`
- **缓存统计**: `/actuator/caches`

## 测试覆盖

### 测试类型
- **单元测试**: Service层业务逻辑测试
- **集成测试**: Controller层API测试
- **压力测试**: 并发1000笔交易测试
- **数据测试**: Repository层数据访问测试

### 测试数据
测试环境自动生成数据：

- **A股交易数据**: 自动生成100-500笔真实A股交易记录
- **多账户数据**: 模拟多个银行账户的交易活动
- **时间序列数据**: 按日期分布的交易数据

## 部署指南

### Docker部署

详细的Docker部署说明请参考：[Docker构建指南](DOCKER_BUILD_GUIDE.md)

### 生产环境部署

1. **构建生产版本**
```bash
mvn clean package -DskipTests -Pprod
```

2. **Docker部署**
```bash
# 构建镜像
docker build -t transaction-management .

# 运行容器
docker run -d -p 8080:8080 \
  --name transaction-management \
  --restart unless-stopped \
  transaction-management
```

3. **使用Docker Compose**
```bash
docker-compose up -d
```

### 环境配置

生产环境可以通过环境变量配置：

```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/transactiondb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=password

# JVM配置
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# 应用配置
SPRING_PROFILES_ACTIVE=prod
```