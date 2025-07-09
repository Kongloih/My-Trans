# 本地构建Dockerfile - 避免容器内网络问题
# FROM openjdk:21-jre-slim
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS builder

# 设置工作目录
WORKDIR /app

# 创建用户
RUN addgroup --system banking && adduser --system --ingroup banking banking

# 复制本地构建的JAR文件
COPY target/transaction-management-*.jar app.jar

# 设置权限
RUN chown -R banking:banking /app

# 切换到用户
USER banking

# 暴露端口
EXPOSE 8080

# JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 