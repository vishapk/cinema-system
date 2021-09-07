select * from shows s
where current_timestamp between s.start_time and s.end_time
ORDER BY start_time asc
