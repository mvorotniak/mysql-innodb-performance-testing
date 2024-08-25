ALTER TABLE users
DROP INDEX idx_birth_date;

ALTER TABLE users
ADD INDEX idx_birth_date_hash (birth_date) USING HASH;