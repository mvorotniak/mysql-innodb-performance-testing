ALTER TABLE users
DROP INDEX idx_birth_date_hash;

ALTER TABLE users
ADD INDEX idx_birth_date (birth_date);
