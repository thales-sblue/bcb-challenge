# 1) Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# só copiar o mínimo para aproveitar cache do Maven
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

# código-fonte
COPY src src

# build do jar
RUN chmod +x mvnw \
 && ./mvnw clean package -DskipTests

# 2) Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# instala bash (para o wait-for-it) e o cliente pg_isready
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      bash \
      postgresql-client \
 && rm -rf /var/lib/apt/lists/*

# copia o jar gerado pelo build
COPY --from=build /app/target/*.jar app.jar

# copia os scripts de espera e entrypoint
COPY wait-for-it.sh entrypoint.sh ./
RUN chmod +x wait-for-it.sh entrypoint.sh

# libera a porta da API
EXPOSE 8080

# entrypoint que vai aguardar os serviços
ENTRYPOINT ["./entrypoint.sh"]
