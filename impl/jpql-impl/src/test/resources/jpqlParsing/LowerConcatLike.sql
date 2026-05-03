SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :term, '%'));
