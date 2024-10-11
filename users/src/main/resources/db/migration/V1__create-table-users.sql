create table users (
id bigint auto_increment not null,
username varchar(255) not null,
email varchar(255) not null,
password varchar(255) not null,
primary key(id)
);