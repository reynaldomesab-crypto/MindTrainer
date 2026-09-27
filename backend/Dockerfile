# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy built jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]