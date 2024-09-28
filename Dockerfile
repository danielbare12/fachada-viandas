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

docker run -d --name dd-agent \
-e DD_API_KEY=cfdac9393fc9ef617570532c892a072a \
-e DD_SITE="us5.datadoghq.com" \
-v /var/run/docker.sock:/var/run/docker.sock:ro \
-v /proc/:/host/proc/:ro \
-v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro \
-v /var/lib/docker/containers:/var/lib/docker/containers:ro \
gcr.io/datadoghq/agent:7

COPY --from=build /target/TPDDSApp.jar TPDDSApp.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-classpath","TPDDSApp.jar","ar.edu.utn.dds.k3003.app.WebApp"]
