FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

ADD target/DatabaseBrokerService-0.0.1-SNAPSHOT.jar StudentMicroservice.jar

ENTRYPOINT ["java","-jar", "StudentMicroservice.jar"]