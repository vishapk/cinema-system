SELECT * FROM shows s
WHERE s.end_time < current_timestamp
ORDER BY start_time asc
