CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    age INTEGER CHECK (age >= 0),
    status CHAR(1) DEFAULT 'A',
    balance DECIMAL(10, 2),
    created_at TIMESTAMP
);

CREATE TABLE orders (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total NUMERIC(12, 2),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE SET NULL
);

CREATE TABLE t (
    id INT,
    name VARCHAR(50),
    CONSTRAINT pk PRIMARY KEY (id),
    UNIQUE (name),
    CHECK (id > 0)
);

CREATE TABLE ref_test (
    id INTEGER,
    ref_id INTEGER REFERENCES other_table (id) ON DELETE RESTRICT ON UPDATE NO ACTION
);

CREATE TABLE types_test (
    price DOUBLE PRECISION,
    name CHARACTER VARYING(100)
);
