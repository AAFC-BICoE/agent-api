CREATE SCHEMA agent;

GRANT USAGE ON SCHEMA agent TO $spring_datasource_username;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA agent TO $spring_datasource_username;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA agent TO $spring_datasource_username;

alter default privileges in schema agent grant SELECT, INSERT, UPDATE, DELETE on tables to $spring_datasource_username;
alter default privileges in schema agent grant all on sequences to $spring_datasource_username;
