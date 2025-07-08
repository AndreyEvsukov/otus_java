-- Tables
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table address
(
    client_id bigint not null,
    street varchar(50),
    CONSTRAINT fk_address_client_id FOREIGN KEY (client_id)
    REFERENCES client(id) ON DELETE CASCADE
);

create table phone
(
    id  bigserial not null primary key,
    client_id bigint not null,
    number varchar(50),
    CONSTRAINT fk_phone_client_id FOREIGN KEY (client_id)
    REFERENCES client(id) ON DELETE CASCADE
);
