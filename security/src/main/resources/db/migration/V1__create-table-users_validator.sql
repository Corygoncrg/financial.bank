create table users_validator (
id bigint auto_increment not null,
uuid varchar(255) not null,
id_user bigint not null,
expiration_date TIMESTAMP not null,
primary key(id),
foreign key (id_user) references users(id)
);