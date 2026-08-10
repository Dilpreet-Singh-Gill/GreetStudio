# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Install fontconfig and fonts for Java2D image generation
RUN apk add --no-cache fontconfig ttf-dejavu

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
