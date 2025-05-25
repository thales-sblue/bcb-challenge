# 1) Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia arquivos essenciais para cache do Maven
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

# Copia código-fonte
COPY src src

# Build do JAR
RUN chmod +x mvnw \
 && ./mvnw clean package -DskipTests

# 2) Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Instala dependências necessárias (bash, postgres client e dos2unix para conversão)
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      bash \
      postgresql-client \
      dos2unix \
 && rm -rf /var/lib/apt/lists/*

# Copia o jar do build stage
COPY --from=build /app/target/*.jar app.jar

# Copia os scripts
COPY wait-for-it.sh entrypoint.sh ./

# Corrige quebras de linha Windows -> Linux e aplica permissão de execução
RUN dos2unix wait-for-it.sh entrypoint.sh \
 && chmod +x wait-for-it.sh entrypoint.sh

# Expõe a porta da API
EXPOSE 8080

# Executa entrypoint que aguarda os serviços estarem prontos
ENTRYPOINT ["./entrypoint.sh"]
