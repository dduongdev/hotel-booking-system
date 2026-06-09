# ==========================
# STAGE 1: BUILD WITH MAVEN
# ==========================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for better layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B -q

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests -B -q

# ==========================
# STAGE 2: RUN WITH JRE
# ==========================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check (check the Java process is running)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD pgrep -f "hotel" || exit 1

# Run with production profile by default
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
