\c product_management_db

create table product (
    id serial primary key,
    name varchar(100),
    price int,
    creation_datetime timestamp
);

create table product_category (
    id serial primary key,
    name varchar(100),
    product_id int not null,
    constraint fk_product foreign key (product_id) references product(id)
);
