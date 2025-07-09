#!/bin/bash

# 银行交易管理系统 - 快速开始脚本

echo "🚀 银行交易管理系统 - 快速开始"
echo "=================================="

# 检查Java版本
echo "📋 检查Java版本..."
java -version
if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到Java，请安装Java 21"
    exit 1
fi

# 检查Maven版本
echo "📋 检查Maven版本..."
mvn -version
if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到Maven，请安装Maven 3.6+"
    exit 1
fi

# 检查Docker版本
echo "📋 检查Docker版本..."
docker --version
if [ $? -ne 0 ]; then
    echo "⚠️  警告: 未找到Docker，将使用本地运行模式"
    DOCKER_MODE=false
else
    DOCKER_MODE=true
fi

echo ""
echo "🔨 开始构建项目..."

# 清理并构建项目
echo "📦 构建项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 构建失败"
    exit 1
fi

echo "✅ 项目构建成功"

if [ "$DOCKER_MODE" = true ]; then
    echo ""
    echo "🐳 构建Docker镜像..."
    docker build -t transaction-management .
    if [ $? -ne 0 ]; then
        echo "❌ Docker构建失败"
        exit 1
    fi
    
    echo "✅ Docker镜像构建成功"
    echo ""
    echo "🚀 启动Docker容器..."
    docker run -d -p 8080:8080 --name transaction-management transaction-management
    
    echo ""
    echo "🎉 应用启动成功！"
    echo "📱 访问地址: http://localhost:8080"
    echo "📚 API文档: http://localhost:8080/swagger-ui.html"
    echo "💚 健康检查: http://localhost:8080/actuator/health"
    echo ""
    echo "📋 常用命令:"
    echo "  查看日志: docker logs -f transaction-management"
    echo "  停止应用: docker stop transaction-management"
    echo "  重启应用: docker restart transaction-management"
    echo "  删除容器: docker rm transaction-management"
else
    echo ""
    echo "🚀 本地启动应用..."
    echo "📱 应用将在 http://localhost:8080 启动"
    echo "📚 API文档: http://localhost:8080/swagger-ui.html"
    echo "💚 健康检查: http://localhost:8080/actuator/health"
    echo ""
    echo "按 Ctrl+C 停止应用"
    
    mvn spring-boot:run
fi 