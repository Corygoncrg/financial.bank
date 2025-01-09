FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

# Copy the local Maven repository into the container

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]