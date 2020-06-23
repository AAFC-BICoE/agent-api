REVOKE CONNECT ON DATABASE person_test FROM PUBLIC;
REVOKE USAGE, CREATE ON SCHEMA public FROM PUBLIC;

CREATE SCHEMA IF NOT EXISTS person;
REVOKE CREATE ON SCHEMA person FROM PUBLIC;

-- migration user
CREATE ROLE migration_user NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN PASSWORD 'test';
GRANT CONNECT ON DATABASE person_test TO migration_user;
GRANT USAGE, CREATE ON SCHEMA person TO migration_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA person GRANT ALL PRIVILEGES ON TABLES TO migration_user;

-- web user
CREATE ROLE web_user NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN PASSWORD 'test';
GRANT CONNECT ON DATABASE person_test TO web_user;
GRANT USAGE ON SCHEMA person TO web_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA person TO web_user;
ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA person GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO web_user;
ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA person GRANT SELECT, USAGE ON SEQUENCES TO web_user;
