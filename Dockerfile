FROM maven:3.9.9-eclipse-temurin-21

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application and package it as a JAR
RUN mvn clean package -DskipTests

WORKDIR /app

# Copy the built JAR file from the build stage
COPY  /target/templatelibrary_NoSQL-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
