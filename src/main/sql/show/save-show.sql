INSERT INTO shows(start_time, end_time, movie_id, price)
VALUES (:tart_time, :end_time, :movie_id, :price)
    returning *;
