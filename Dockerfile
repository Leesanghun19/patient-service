# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

# uploads 디렉토리 생성 및 권한 설정
RUN mkdir -p /app/uploads/images && chmod -R 755 /app/uploads

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]