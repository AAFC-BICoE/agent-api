FROM maven:3.6.3-jdk-11-slim as build-stage

RUN mkdir /project
WORKDIR /project

# Cache maven dependencies
ADD pom.xml /project
RUN mvn clean install -Dmaven.test.skip=true -Dspring-boot.repackage.skip

# Stage 1: build jar
ADD . /project
# Integration Tests will be skipped as they require a database
RUN mvn test
RUN mvn clean install -Dmaven.test.skip=true

# Stage 2: extract jar and set entrypoint
FROM openjdk:11-jre-slim
RUN useradd -s /bin/bash user
USER root

WORKDIR /app
COPY --from=build-stage --chown=user /project/target/agent-api*.jar /app/
COPY --chown=user scripts/*.sh /app/
COPY --chown=user scripts/*.awk /app/
COPY --chown=user pom.xml /app/
RUN chmod +x *.sh

USER user
EXPOSE 8080
WORKDIR /app

ENV spring.datasource.username=springuser
ENV spring.datasource.password=springcreds
ENV spring.liquibase.user=liquibaseuser
ENV spring.liquibase.password=liquibasecreds
ENV spring.liquibase.defaultSchema=objectstore
ENV POSTGRES_DB=object_store
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=databasecreds
ENV POSTGRES_HOST=localhost

ENTRYPOINT ["java", "-jar", "/agent-api.jar"]
