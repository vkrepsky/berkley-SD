# Start with a base image containing Java runtime (Java 17)
FROM openjdk:17-jdk-slim as build

# Set the current working directory inside the image
WORKDIR /app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Load all dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the project source
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]
