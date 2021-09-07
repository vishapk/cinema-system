package com.demo.book.ticket.exception

class NoTicketsAvailableException(override val message: String?) : RuntimeException(message)
