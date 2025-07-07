-- Tables
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table address
(
    client_id bigint not null,
    street varchar(50)
);

create table phone
(
    id  bigserial not null primary key,
    client_id bigint not null,
    number varchar(50)
);
