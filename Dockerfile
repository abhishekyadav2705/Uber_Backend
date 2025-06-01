# Stage 1: Build the app
FROM maven:3.9.3-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/uber_backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
