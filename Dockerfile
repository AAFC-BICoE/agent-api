FROM openjdk:11-jre-slim

RUN useradd -s /bin/bash user
USER user
COPY --chown=644 target/agent-api-*.jar /agent-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/agent-api.jar"]

