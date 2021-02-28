CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

CREATE TABLE airdrop_event (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    room_id VARCHAR(256) NOT NULL,
    token CHAR(3) NOT NULL,
    total_amount INTEGER UNSIGNED NOT NULL,
    created_at BIGINT UNSIGNED,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
SHOW WARNINGS;

CREATE UNIQUE INDEX ix_airdrop_event_user_id_room_id_token ON airdrop_event (user_id, room_id, token);
SHOW WARNINGS;

CREATE UNIQUE INDEX ix_airdrop_event_room_id_token ON airdrop_event (room_id, token);
SHOW WARNINGS;

CREATE TABLE airdrop_recipient (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    event_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED,
    amount INTEGER UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(event_id) REFERENCES airdrop_event (id)
) ENGINE=InnoDB;
SHOW WARNINGS;

CREATE INDEX ix_airdrop_recipient_event_id_user_id ON airdrop_recipient (event_id, user_id);
SHOW WARNINGS;

CREATE USER test identified by '1234';
GRANT SELECT, INSERT, UPDATE, DELETE on test_db.* to test;
FLUSH PRIVILEGES;
