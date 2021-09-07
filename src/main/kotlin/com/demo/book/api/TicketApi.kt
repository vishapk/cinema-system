package com.demo.book.api

import com.demo.book.ticket.service.TicketService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.inject.Inject

@Controller
class TicketApi(@Inject val ticketService: TicketService) {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post("/show/{showId}/ticket")
    fun saveMovie(showId: Int): MutableHttpResponse<Int> {
        return HttpResponse.ok(ticketService.bookTicket(showId))
    }
}
