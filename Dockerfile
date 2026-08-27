# Stage 1: Build
FROM docker.m.daocloud.io/library/maven:3.8-openjdk-8 AS builder
WORKDIR /app
COPY pom.xml .
# 预下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM docker.m.daocloud.io/library/openjdk:8-jre-slim
LABEL maintainer="wuyali <402161052@qq.com>"
WORKDIR /app

COPY --from=builder /app/target/idp-0.0.1-SNAPSHOT-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
