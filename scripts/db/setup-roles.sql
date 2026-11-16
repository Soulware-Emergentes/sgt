-- Sets up the two roles this project uses instead of one shared
-- credential, so DDL (schema changes) and DML (application queries)
-- are handled by different roles with different privileges:
--
--   sgt_migrator - owns the schema, runs Flyway migrations (DDL rights)
--   sgt_app      - used by the Spring Boot app at runtime (DML rights only)
--
-- Run as the postgres superuser, e.g. on Fedora:
--   sudo -iu postgres psql -f scripts/db/setup-roles.sql
--
-- Passwords are read from environment variables so nothing sensitive
-- ends up in shell history or this file. Set them before running:
--   export SGT_MIGRATOR_PASSWORD='...'
--   export SGT_APP_PASSWORD='...'
-- (use the same values you put in your local .env, see .env.example)

\set migrator_pw `echo "${SGT_MIGRATOR_PASSWORD:?SGT_MIGRATOR_PASSWORD not set}"`
\set app_pw `echo "${SGT_APP_PASSWORD:?SGT_APP_PASSWORD not set}"`

CREATE ROLE sgt_migrator WITH LOGIN PASSWORD :'migrator_pw';
CREATE ROLE sgt_app WITH LOGIN PASSWORD :'app_pw';

-- sgt_migrator owns the database, so it can create/alter/drop objects
-- in it (that's what Flyway needs to run migrations).
CREATE DATABASE sgt OWNER sgt_migrator;

\connect sgt

-- PostGIS is not a trusted extension, so only a superuser can create it.
-- Creating it here keeps sgt_migrator free of that privilege, and
-- migrations use its types and functions as if they were built in.
CREATE EXTENSION postgis;

-- sgt_app only needs to connect and use the public schema - it does not
-- own anything here, so by default it has no privileges on the tables
-- sgt_migrator creates (ownership does not imply privileges in Postgres).
GRANT CONNECT ON DATABASE sgt TO sgt_app;
GRANT USAGE ON SCHEMA public TO sgt_app;

-- Grant DML on every table/sequence sgt_migrator creates from now on,
-- automatically. Without this, each new Flyway migration would require
-- a manual GRANT before sgt_app could use the new table.
ALTER DEFAULT PRIVILEGES FOR ROLE sgt_migrator IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO sgt_app;

ALTER DEFAULT PRIVILEGES FOR ROLE sgt_migrator IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO sgt_app;
