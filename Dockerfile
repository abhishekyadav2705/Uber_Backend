# Stage 1: Build the app with Maven and Java 21
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Install Maven manually (since no official Maven+Java21 image yet)
RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the app with Java 21 runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/uber_backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
