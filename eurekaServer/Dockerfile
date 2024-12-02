FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN apt-get update && apt-get install -y curl
ENTRYPOINT ["java", "-jar", "app.jar"]