package com.demo.book.admin.exception

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
    Requires(classes = [UsernameNotFoundException::class, ExceptionHandler::class])
)
class UsernameNotFoundExceptionHandler : ExceptionHandler<UsernameNotFoundException, HttpResponse<ApiErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>?,
        exception: UsernameNotFoundException
    ): HttpResponse<ApiErrorResponse> {
        return HttpResponse.notFound(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "Username Not found",
                message = exception.message.toString()
            )
        )
    }
}
