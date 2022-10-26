FROM openjdk:8-jdk-alpine
EXPOSE 8084
ADD /target/user-service-1.0.jar user-service-1.0.jar
ENTRYPOINT ["java", "-jar", "user-service-1.0.jar"]