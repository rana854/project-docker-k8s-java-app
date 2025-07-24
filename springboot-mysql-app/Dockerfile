# Use a base image with JDK
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the project JAR file (build it first using mvn package)
COPY target/springboot-mysql-app-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
