ALTER TABLE movies
    ADD COLUMN duration_in_minutes INTEGER;

Update movies
    SET duration_in_minutes=
        round(ABS((EXTRACT(EPOCH FROM end_time) - EXTRACT(EPOCH FROM start_time))/60));

ALTER TABLE movies
    DROP COLUMN start_time;

ALTER TABLE movies
    DROP COLUMN end_time;

ALTER TABLE movies
   ALTER COLUMN duration_in_minutes SET NOT NULL;

ALTER TABLE movies
    ADD CONSTRAINT unique_title UNIQUE (title);

