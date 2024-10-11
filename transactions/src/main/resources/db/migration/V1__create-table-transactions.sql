create table transactions (
id bigint not null auto_increment,
originalBank varchar(255) not null,
originalAgency int not null,
originalAccount varchar(20) not null,
destinyBank varchar(255) not null,
destinyAgency int not null,
destinyAccount varchar(20) not null,
amount decimal(15, 2) not null,
transaction_time DATETIME not null,
primary key(id)
);