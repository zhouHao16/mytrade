FROM openjdk:21-jdk-slim
LABEL maintainer="example@example.com"

WORKDIR /app

COPY target/*.jar app.jar

# 暴露8080端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 