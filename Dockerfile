# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:21-alpine-jdk AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -Pprod

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 3118

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
