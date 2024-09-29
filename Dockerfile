# syntax = docker/dockerfile:1.2
#
# Build stage
#
FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim

COPY --from=build /target/TPDDSApp.jar TPDDSApp.jar

# ENV PORT=8080

FROM openjdk:17-jdk-alpine

RUN apt-get update && apt-get install -y datadog-agent

# Instalar el Datadog Agent
COPY datadog-agent /usr/local/datadog/
RUN chmod +x /usr/local/datadog/datadog-agent

# Configurar el Datadog Agent (ejemplo)
ENV DD_API_KEY=cfdac9393fc9ef617570532c892a072a
ENV DD_TAGS="env:production,service:myapp"

# Iniciar el Datadog Agent
CMD ["/usr/local/datadog/datadog-agent", "-d"]

EXPOSE 8080
ENTRYPOINT ["java","-classpath","TPDDSApp.jar","ar.edu.utn.dds.k3003.app.WebApp"]
