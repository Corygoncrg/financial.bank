ALTER TABLE transactions
ADD COLUMN id_user BIGINT NOT NULL,
ADD FOREIGN KEY (id_user) REFERENCES users(id);