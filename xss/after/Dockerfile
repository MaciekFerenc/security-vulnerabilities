# Build stage
FROM openjdk:21-jdk as builder

WORKDIR /app

# Copy source code
COPY . .

# Build the application (adjust for Maven or Gradle)
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]