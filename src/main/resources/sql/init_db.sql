\c postgres
CREATE DATABASE product_management_db;
CREATE USER product_manager_user WITH PASSWORD '123456';

grant connect on database product_management_db to product_manager_user;

\c product_management_db
grant create on schema public
    to product_manager_user;

alter default privileges in schema public
    grant select, update, delete on tables to product_manager_user;

alter default privileges in schema public
    grant usage, update, select on sequences to product_manager_user;