CREATE TABLE users (
    username VARCHAR(100) NOT NULL ,
    password VARCHAR(100) NOT NULL ,
    name VARCHAR(100) NOT NULL ,
    token VARCHAR(100),
    token_expired_at BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (username),
    UNIQUE (token)
) engine = InnoDB;

SELECT * FROM users;

CREATE TABLE contacts (
    id VARCHAR(100) NOT NULL ,
    username VARCHAR(100) NOT NULL ,
    first_name VARCHAR(100) NOT NULL ,
    last_name VARCHAR(100),
    phone VARCHAR(100),
    email VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY fk_users_contacts (username) REFERENCES users (username)
) ENGINE = InnoDB;

SELECT * FROM contacts;

ALTER TABLE contacts
ADD COLUMN created_at TIMESTAMP;

ALTER TABLE contacts
    ADD COLUMN updated_at TIMESTAMP;

CREATE TABLE addresses (
    id VARCHAR(100) NOT NULL ,
    contact_id VARCHAR(100) NOT NULL ,

    street VARCHAR(200),
    city VARCHAR(100),
    province VARCHAR(100),
    country VARCHAR(100) NOT NULL ,
    postal_code VARCHAR(10),

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY fk_contacts_addresses (contact_id) REFERENCES contacts(id)
) ENGINE = InnoDB;

SELECT * FROM addresses;

ALTER TABLE contacts
ADD UNIQUE (first_name);

ALTER TABLE contacts
    ADD UNIQUE (last_name);

DESC contacts;

SHOW INDEX FROM contacts;

SHOW CREATE TABLE contacts;

DELETE FROM addresses;
DELETE FROM contacts;
DELETE FROM users;