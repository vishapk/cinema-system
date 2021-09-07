package com.demo.book

import com.demo.book.admin.service.AdminService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Authentication(@Inject private val adminService: AdminService) : AuthenticationProvider {
    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null && authenticationRequest.identity != null && authenticationRequest.secret != null) { // ktlint-disable max-line-length
            if (adminService.isAuthorized(
                    authenticationRequest.identity as String,
                    authenticationRequest.secret as String
                )
            ) {
                return Flowable.just(
                    UserDetails(
                        authenticationRequest.identity as String,
                        ArrayList()
                    )
                )
            }
        }
        return Flowable.just(AuthenticationFailed())
    }
}
