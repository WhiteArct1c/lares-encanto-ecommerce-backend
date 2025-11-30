FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install maven -y

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-jammy

COPY --from=build target/lares-encanto-rest-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]