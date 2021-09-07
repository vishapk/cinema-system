package com.demo.book.api

import com.demo.book.movie.entity.Movie
import com.demo.book.movie.service.MovieService
import com.demo.book.movie.request.MovieRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.inject.Inject

@Controller
class MovieApi(@Inject val movieService: MovieService) {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/movies")
    fun allMovies(): HttpResponse<List<Movie>> {
        return HttpResponse.ok(movieService.allMovies())
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post("/movies")
    fun saveMovie(@Body movieRequest: MovieRequest): MutableHttpResponse<Int> {
        return HttpResponse.ok(movieService.save(movieRequest).id)
    }
}
