UPDATE shows SET available_tickets = available_tickets-1 WHERE id = :show_id
    returning *;

