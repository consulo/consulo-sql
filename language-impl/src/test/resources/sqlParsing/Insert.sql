INSERT INTO users (id, name, email) VALUES (1, 'John', 'john@example.com');
INSERT INTO users VALUES (1, 'John', 'john@example.com');
INSERT INTO users (name) VALUES ('Alice'), ('Bob'), ('Charlie');
INSERT INTO logs (message) SELECT name FROM users;
INSERT INTO users DEFAULT VALUES;
