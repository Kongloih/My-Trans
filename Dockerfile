# 使用Docker官方镜像（推荐）
FROM openjdk:21-jre-slim

# 或者使用阿里云公共镜像路径
FROM registry.cn-hangzhou.aliyuncs.com/openjdk/openjdk:21-jre-slim

# 设置维护者信息
LABEL maintainer="Banking System <banking@example.com>"
LABEL description="Banking Transaction Management System"
LABEL version="1.0.0"

# 设置工作目录
WORKDIR /app

# 复制Maven包装器文件
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# 下载依赖（利用Docker层缓存）
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src/ src/

# 构建应用程序
RUN ./mvnw clean package -DskipTests

# 创建运行时镜像
FROM openjdk:21-jre-slim
# FROM registry.cn-hangzhou.aliyuncs.com/openjdk/openjdk:21-jre-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用程序用户
RUN groupadd -r banking && useradd -r -g banking banking

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=0 /app/target/transaction-management-*.jar app.jar

# 更改文件所有者
RUN chown -R banking:banking /app

# 切换到应用程序用户
USER banking

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用程序
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 