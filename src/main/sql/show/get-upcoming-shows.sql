select * from shows s
where s.start_time > current_timestamp
ORDER BY start_time asc
