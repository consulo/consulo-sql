DELETE FROM users WHERE id = 1;
DELETE FROM users WHERE status = 'inactive' AND last_login IS NULL;
DELETE FROM logs;
DELETE FROM orders o WHERE o.total = 0;
