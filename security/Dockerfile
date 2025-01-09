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

COPY security/.mvn /app/.mvn
COPY security/mvnw /app/mvnw
COPY security/pom.xml /app/pom.xml
COPY security/src /app/src

COPY --from=builder /root/.m2 /root/.m2

CMD ["./mvnw", "-Dmaven.repo.local=/root/.m2/repository", "spring-boot:run"]