FROM openjdk:17
LABEL authors="slava"
WORKDIR /app
COPY ./target/UfanetTask-0.0.1-SNAPSHOT.jar /app/my-app-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/my-app-1.0.0.jar"]