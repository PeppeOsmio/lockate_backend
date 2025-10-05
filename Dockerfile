# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:25-alpine3.22-jdk AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:25-alpine3.22

WORKDIR /app

COPY --from=builder /app/target/*.jar lockate.jar

EXPOSE 3118

# Run the application
ENTRYPOINT ["java", "-jar", "lockate.jar"]
