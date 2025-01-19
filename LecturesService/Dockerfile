
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

ADD target/LecturesService-0.0.1-SNAPSHOT.jar StudentMicroservice.jar

ENTRYPOINT ["java","-jar", "StudentMicroservice.jar"]