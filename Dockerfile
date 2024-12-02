FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/RedisChat-0.0.1-SNAPSHOT.jar /app/RedisChat.jar

ENTRYPOINT ["java", "-jar", "/app/RedisChat.jar"]
