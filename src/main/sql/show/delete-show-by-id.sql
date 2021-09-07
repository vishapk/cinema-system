DELETE FROM shows s
WHERE s.id = :show_id
RETURNING *;
