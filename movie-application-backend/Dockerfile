# Stage 1 - build using Maven base image
FROM maven:3.9.6 AS builder
WORKDIR /app

# Copy pom and sources, build a fat JAR
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2 - runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]