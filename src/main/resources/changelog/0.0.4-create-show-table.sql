CREATE TABLE shows (
id SERIAL PRIMARY KEY,
start_time TIMESTAMP NOT NULL,
end_time TIMESTAMP NOT NULL,
movie_id INT NOT NULL,
CONSTRAINT fk_constraint FOREIGN KEY(movie_id) REFERENCES movies(id)
);


