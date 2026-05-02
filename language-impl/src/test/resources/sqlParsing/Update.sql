UPDATE users SET name = 'Jane' WHERE id = 1;
UPDATE users SET name = 'Jane', email = 'jane@example.com' WHERE id = 1;
UPDATE users u SET u.status = 'inactive' WHERE u.last_login < '2020-01-01';
UPDATE orders SET total = price * quantity;
UPDATE users SET name = 'test';
