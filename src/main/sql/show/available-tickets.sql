SELECT shows.start_time , shows.available_tickets, shows.id, shows.end_time, shows.movie_id, shows.capacity,shows.price FROM shows
    INNER JOIN movies ON shows.movie_id =movies.id and movies.title like :title
    WHERE shows.available_tickets >= 0
    ORDER BY start_time ASC

