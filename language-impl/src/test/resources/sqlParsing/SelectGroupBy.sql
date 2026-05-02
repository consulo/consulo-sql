SELECT status, COUNT(*) FROM users GROUP BY status;
SELECT status, COUNT(*) FROM users GROUP BY status HAVING COUNT(*) > 10;
SELECT * FROM users ORDER BY name;
SELECT * FROM users ORDER BY name ASC;
SELECT * FROM users ORDER BY created_at DESC;
SELECT * FROM users ORDER BY name ASC, id DESC;
SELECT department, role, COUNT(*) FROM employees GROUP BY department, role HAVING COUNT(*) > 5 ORDER BY department ASC;
