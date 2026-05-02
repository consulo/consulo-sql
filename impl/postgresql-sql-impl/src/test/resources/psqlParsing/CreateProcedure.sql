CREATE PROCEDURE insert_data(name VARCHAR, age INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO users (name, age) VALUES (name, age);
END;
$$;
