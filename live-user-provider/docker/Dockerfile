FROM openjdk:17-jdk-slim
COPY target/live-user-provider-docker.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]