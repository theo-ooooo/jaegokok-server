# ── Build stage ──────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

COPY jaegokok-common jaegokok-common
COPY jaegokok-core    jaegokok-core
COPY jaegokok-domain  jaegokok-domain
COPY jaegokok-infra   jaegokok-infra
COPY jaegokok-api     jaegokok-api

RUN chmod +x gradlew && ./gradlew :jaegokok-api:bootJar -x test --no-daemon

# ── Runtime stage ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/jaegokok-api/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
