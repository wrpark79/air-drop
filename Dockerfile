FROM openjdk:11-jdk AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder build/libs/*.jar airdrop.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom --spring.config.location=file:/app/application.properties -jar /app/airdrop.jar"]
