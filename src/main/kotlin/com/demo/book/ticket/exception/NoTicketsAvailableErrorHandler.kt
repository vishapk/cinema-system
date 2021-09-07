package com.demo.book.ticket.exception

import com.demo.book.ApiErrorResponse
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(
    Requires(classes = [NoTicketsAvailableException::class, ExceptionHandler::class])
)
class NoTicketsAvailableErrorHandler : ExceptionHandler<NoTicketsAvailableException, HttpResponse<ApiErrorResponse>> {
    override fun handle(request: HttpRequest<*>?, exception: NoTicketsAvailableException): HttpResponse<ApiErrorResponse> { // ktlint-disable max-line-length
        return HttpResponse.badRequest(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "No ticket available error",
                message = exception.message.toString()
            )
        )
    }
}
