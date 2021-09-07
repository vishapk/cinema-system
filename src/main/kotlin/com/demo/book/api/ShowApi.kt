package com.demo.book.api

import com.demo.book.show.entity.Show
import com.demo.book.show.request.*
import com.demo.book.show.service.ShowService
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.inject.Inject

@Controller
class ShowApi(@Inject val showService: ShowService) {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post("/shows")
    fun saveShows(@Body showRequest: ShowRequest): MutableHttpResponse<Int> {
        return HttpResponse.ok(showService.save(showRequest).id)
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put("/shows")
    fun savePrice(showPriceRequest: ShowPriceRequest): MutableHttpResponse<Show> {
        return HttpResponse.ok(
            showService.setShowPrice(
                showPriceRequest
            )
        )
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Delete(uris = ["/shows/{id}"])
    fun deleteShowById(id: Int): MutableHttpResponse<Int> {
        return HttpResponse.ok(showService.deleteShowById(DeleteShowRequest(id)).id)
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/shows")
    fun pastShows(@Nullable @QueryValue status: String?): HttpResponse<List<Show>> {
        return HttpResponse.ok(showService.findShowsByStatus(status))
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/movies/{movieTitle}/shows")
    fun getMovieByTitle(movieTitle: String): MutableHttpResponse<List<Show>> {
        return HttpResponse.ok(showService.viewAvailableTickets(movieTitle))
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(uris = ["/shows/{showId}/tickets"])
    fun getShowWithTicket(@PathVariable showId: Int): MutableHttpResponse<Int> {
        return HttpResponse.ok(showService.viewAvailableTicketsForShow(FindTicketForShowRequest(showId)))
    }
}
