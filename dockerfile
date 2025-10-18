# Use a Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy source code and build it
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Now build a lightweight runtime image
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy only the built jar file
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]