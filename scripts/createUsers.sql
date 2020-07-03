CREATE USER $spring_datasource_username NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN PASSWORD '$spring_datasource_password';
GRANT CONNECT ON DATABASE $POSTGRES_DB TO $spring_datasource_username;

CREATE USER $spring_liquibase_user NOSUPERUSER NOCREATEROLE INHERIT LOGIN PASSWORD '$spring_liquibase_password';
GRANT CONNECT, CREATE ON DATABASE $POSTGRES_DB TO $spring_liquibase_user;
GRANT ALL PRIVILEGES ON DATABASE $POSTGRES_DB TO $spring_liquibase_user;