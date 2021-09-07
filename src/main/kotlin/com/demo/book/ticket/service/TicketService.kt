package com.demo.book.ticket.service

import com.demo.book.exceptions.InvalidInputException
import com.demo.book.exceptions.InvalidOperationException
import com.demo.book.show.repository.ShowRepository
import com.demo.book.ticket.exception.InvalidDateException
import com.demo.book.ticket.exception.NoTicketsAvailableException
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketService(
    @Inject private val showRepository: ShowRepository
) {
    fun bookTicket(showId: Int): Int {
        val show = try {
            showRepository.findShowById(showId)
        } catch (e: Exception) {
            throw InvalidInputException("No Shows are available")
        }

        if (show.price == null) {
            throw InvalidOperationException("Show price is not available")
        }

        if (show.startTime.isBefore(LocalDateTime.now())) {
            throw InvalidDateException("Cannot book ticket for show before today's date")
        }

        if (show.startTime.isAfter(LocalDateTime.now().plusDays(7))) {
            throw InvalidDateException("Cannot book ticket for show beyond 7 days")
        }

        if (show.availableTickets <= 0) {
            throw NoTicketsAvailableException("No tickets available")
        }

        val ticket = showRepository.bookTicket(show.id)

        return ticket.ticketNo + 1
    }
}
