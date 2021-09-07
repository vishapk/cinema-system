package com.demo.book.exceptions

import com.demo.book.ApiErrorResponse
import com.demo.book.show.exception.ShowNotFoundException
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
    Requires(classes = [ShowNotFoundException::class, ExceptionHandler::class])
)
class ShowNotFoundExceptionHandler : ExceptionHandler<ShowNotFoundException, HttpResponse<ApiErrorResponse>> {
    override fun handle(request: HttpRequest<*>?, exception: ShowNotFoundException): HttpResponse<ApiErrorResponse> {
        return HttpResponse.badRequest(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "Show not found exception",
                message = exception.message.toString()
            )
        )
    }
}
