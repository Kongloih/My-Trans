FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app
# 复制本地打包好的 JAR 文件（请替换为实际路径）
COPY target/transaction-management.jar .

# 暴露应用端口（根据实际情况修改）
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "transaction-management.jar"]