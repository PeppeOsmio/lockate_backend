# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:21-alpine-jdk as builder

# Set working directory
WORKDIR /app

# Copy the Maven/Gradle wrapper and configuration first (for caching dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 3118

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
