# ============================================================
# MindCare Service Platform — Docker 镜像
#
# 构建:
#   mvn package -DskipTests
#   docker build -t mindcare-service-platform .
#
# 运行:
#   docker run -p 8080:8080 mindcare-service-platform
# 或使用 docker-compose:
#   docker-compose up -d
# ============================================================

FROM eclipse-temurin:17-jre-alpine

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app

# 复制打包好的 jar
COPY target/mindcare-service-platform-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动（默认使用 docker profile）
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
