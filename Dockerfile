# Stage 1: Build the application jar
FROM gradle:8.13-jdk17 AS builder

WORKDIR /app
COPY src src
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

# Stage 2: Build the runtime image
FROM amazoncorretto:17
WORKDIR /app

COPY --from=builder /app/build/libs/eleweather-app.jar /app/eleweather-app.jar

EXPOSE 3001
CMD ["java", "-jar", "eleweather-app.jar"]
