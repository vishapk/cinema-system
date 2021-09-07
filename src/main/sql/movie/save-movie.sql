INSERT INTO movies(title, duration_in_minutes)
VALUES (:title, :duration_in_minutes)
returning *;
