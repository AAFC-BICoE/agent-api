# Temporary build instructions:

If you are checking out the repo for the first time...

* git clone https://github.com/AAFC-BICoE/agent-api.git
* git checkout Feature_2000_Openshift_Deployment

Execute these steps each time the dev branch must be deployed

* git fetch
* git status   // optional to copy the branch name for pasting later
* git checkout dev 
* git pull   // bring dev up to date
* git tag    // optional to check the correct tag is present
* git checkout Feature_2000_Openshift_Deployment
* git merge dev  

This step will bring up the Nano editor with a default message to the effect of "Branch dev merged into Branch Feature_2000_Openshift_Deployment..."
I usually change it slightly so that I recognize it when building.

Use \<Cntl\>O \<Enter\> to save the Nano editor contents.

Use \<Cntl\>X to exit Nano.

* git push  // requires your GitHub credentials

Go to the OKD site.

Enter the DinaUI project.

Go to the Builds/Builds page and click on 'obj-store-api-build-base'.

Press the 'Start Build' button.

ObjectStoreAPI container will automatically be redeployed with new version when the build completes.


# agent-api

AAFC DINA agent module implementation.

See DINA agent module [specification](https://github.com/DINA-Web/agent-specs).

## Required

* Java 11
* Maven 3.6 (tested)

## Documentation

To generate the complete documentation:
```
mvn clean compile
```

The single HTML page will be available at `target/generated-docs/index.html`

## To Run

For testing purpose or local development a [Docker Compose](https://docs.docker.com/compose/) and `.env` file are available in the `local` folder.

Create a new `docker-compose` and `.env` file from the example:
```
cp local/docker-compose.yml.example docker-compose.yml
cp local/*.env .
```

Start the app (default port is 8082):
```
docker-compose up --build
```

Once the services have started you can access the endpoints at http://localhost:8082/api/v1/agent

Cleanup:
```
docker-compose down
```

## Testing

For testing purposes use the same docker-compose.yml and .env file (from the previous).

### 1. Add a `docker-compose.override.yml` file.

Create an override file to expose the postgres port on your host:
```
version: "3.7"

services:
  agent-db:
    ports:
      - 5432:5432
```

### 2. Launch the database service

```
docker-compose up agent-db
```

To run the integration tests:

```
mvn verify -Dspring.datasource.url=jdbc:postgresql://localhost/agent_test?currentSchema=agent -Dspring.datasource.username=web_user -Dspring.datasource.password=test -Dspring.liquibase.user=migration_user -Dspring.liquibase.password=test checkstyle:check com.github.spotbugs:spotbugs-maven-plugin:check jacoco:check
```

Some integration tests rely on a schema file available on a shared GitHub repo. If there are issues accessing this file, 
the schema validation can be skipped by adding the `-Dtesting.skip-external-schema-validation=true` flag to the above command.
