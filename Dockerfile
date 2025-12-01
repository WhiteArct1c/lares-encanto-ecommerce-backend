FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install maven -y

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-jammy

COPY --from=build target/lares-encanto-rest-api-0.0.1-SNAPSHOT.jar app.jar

# Copia o arquivo de credenciais do Google Cloud
# IMPORTANTE: O arquivo src/main/resources/google-credentials.json deve existir
# Se não existir, o build falhará. Crie o arquivo ou use volume mount no docker-compose
COPY ./google-credentials.json /app/google-credentials.json

# Configura a variável de ambiente para o Google Cloud Vision API
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/google-credentials.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]