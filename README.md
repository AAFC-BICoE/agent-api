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

See DINA agent module [specification](https://dina-web.github.io/agent-specs/).

## Required

* Java 11
* Maven 3.6 (tested)
* Docker 19+ (for running integration tests)

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

Run tests using `mvn verify`. Docker is required, so the integration tests can launch an embedded Postgres test container.
