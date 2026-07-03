# ---------- Stage 1: Build the jar using Maven ----------
FROM maven:3.8.7-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer, speeds up rebuilds)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the jar on a lightweight JRE ----------
FROM eclipse-temurin:11-jre-jammy
WORKDIR /app
COPY --from=build /app/target/ecommerce-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
