create table transactions (
id bigint not null auto_increment,
original_bank varchar(255) not null,
original_agency int not null,
original_account varchar(20) not null,
destiny_bank varchar(255) not null,
destiny_agency int not null,
destiny_account varchar(20) not null,
amount decimal(15, 2) not null,
transaction_time DATETIME not null,
primary key(id)
);