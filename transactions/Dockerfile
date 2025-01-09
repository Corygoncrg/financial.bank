# Stage 1: Build dependencies
FROM eclipse-temurin:17-jdk-focal as builder

WORKDIR /build

COPY shared /build/shared
COPY commonDatabase /build/commonDatabase

WORKDIR /build/shared
RUN ./mvnw clean install -DskipTests

WORKDIR /build/commonDatabase
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY transactions/.mvn /app/.mvn
COPY transactions/mvnw /app/mvnw
COPY transactions/pom.xml /app/pom.xml
COPY transactions/src /app/src

COPY --from=builder /root/.m2 /root/.m2

# Build and run gateway service
CMD ["./mvnw", "-Dmaven.repo.local=/root/.m2/repository", "spring-boot:run"]