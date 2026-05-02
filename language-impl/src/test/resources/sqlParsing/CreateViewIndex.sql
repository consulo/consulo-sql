CREATE VIEW active_users AS SELECT * FROM users WHERE status = 'A';
CREATE VIEW user_orders AS SELECT u.name, o.total FROM users u JOIN orders o ON u.id = o.user_id;
CREATE INDEX idx_name ON users (name);
CREATE UNIQUE INDEX idx_email ON users (email);
CREATE INDEX idx_multi ON orders (user_id, status);
