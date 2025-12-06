\c postgres

CREATE USER product_manager_user WITH PASSWORD '123456';

CREATE DATABASE product_management_db
    OWNER product_manager_user;

\c product_management_db

GRANT ALL PRIVILEGES ON DATABASE product_management_db TO product_manager_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO product_manager_user;