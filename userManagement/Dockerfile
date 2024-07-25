# Assuming this is the structure and the Dockerfile is inside userManagement
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build

COPY src /build/src
COPY pom.xml /build

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Make sure this path matches the output of the Maven build above
COPY --from=build /build/target/*.jar /app/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
