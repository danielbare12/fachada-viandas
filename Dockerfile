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

FROM openjdk:11-jdk-alpine
COPY --from=build /target/TPDDSApp.jar TPDDSApp.jar
# Instalar el Datadog Agent
COPY datadog-agent /usr/local/datadog/
RUN chmod +x /usr/local/datadog/datadog-agent

# Configurar el Datadog Agent (ejemplo)
ENV DD_API_KEY=cfdac9393fc9ef617570532c892a072a
ENV DD_TAGS="env:production,service:myapp"

# Iniciar el Datadog Agent
CMD ["/usr/local/datadog/datadog-agent", "-d"]

FROM openjdk:17-jdk-slim

# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-classpath","TPDDSApp.jar","ar.edu.utn.dds.k3003.app.WebApp"]
