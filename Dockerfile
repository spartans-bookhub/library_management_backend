# Stage 1: Build with Maven
FROM maven:3.9.3-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/library_management_be-1.0-SNAPSHOT.jar library_management_be-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "library_management_be-1.0-SNAPSHOT.jar"]
EXPOSE 9009
