package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.request.MovieRequest
import com.demo.book.utils.get
import com.demo.book.utils.getWithInvalidCredentials
import com.demo.book.utils.post
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException

class MovieApiTest() : BaseIntegrationSpec() {

    init {
        "should save movie" {
            // Given
            val avengersMovie = newMovieRequest("Avengers", 80)
            val ddlj2 = newMovieRequest("ddlj2", 120)
            // When
            val response = createNewMovie(avengersMovie)
            val response2 = createNewMovie(ddlj2)
            // Then
            response.status shouldBe HttpStatus.OK
            response2.body.get() shouldBe 2
        }

        "should give status as unauthorized when try to get movies with wrong credentials" {
            // Given
            createNewMovie(newMovieRequest("Avengers", 80))
            // When
            val exception = shouldThrow<HttpClientResponseException> {
                httpClient.getWithInvalidCredentials<List<Movie>>("/movies")
            }
            // Then
            exception.message shouldBe "Unauthorized"
            exception.status shouldBe HttpStatus.UNAUTHORIZED
        }

        "should get all saved movies" {
            // Given
            createNewMovie(newMovieRequest("Avengers", 80))
            createNewMovie(newMovieRequest("ddlj2", 120))
            // When
            val response = httpClient.get<List<Movie>>("/movies")

            // Then
            response.status shouldBe HttpStatus.OK
            val savedMovies = response.body.get()
            savedMovies.size shouldBe 2
            jsonString(savedMovies[0]) shouldBe """
                |{
                |  "id" : 1,
                |  "title" : "Avengers",
                |  "durationInMinutes" : 80
                |}
            """.trimMargin().trimIndent()
            jsonString(savedMovies[1]) shouldBe """
                |{
                |  "id" : 2,
                |  "title" : "ddlj2",
                |  "durationInMinutes" : 120
                |}
            """.trimMargin().trimIndent()
        }

        "should return empty list if no movies have been saved" {
            val response = httpClient.get<List<Movie>>("/movies")

            response.status shouldBe HttpStatus.OK
            val savedMovies = response.body.get()
            savedMovies.size shouldBe 0
            savedMovies shouldBe emptyList()
        }

        "should throw error if the duration of movie is less than 5 minutes" {
            val avengersMovie = newMovieRequest("Avengers", 4)

            val exception = shouldThrow<HttpClientResponseException> { createNewMovie(avengersMovie) }

            exception.message shouldBe "Movie duration cannot be less than 5 minutes"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should throw error if the duration of movie is more than 6 hours" {
            val avengersMovie = newMovieRequest("Avengers", 361)

            val exception = shouldThrow<HttpClientResponseException> { createNewMovie(avengersMovie) }

            exception.message shouldBe "Movie duration cannot be more than 6 hours"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
        "should throw an error if movie name is duplicate" {
            // Given
            val avengersMovie = newMovieRequest("Avengers", 80)
            val avengersMovie2 = newMovieRequest("Avengers", 120)
            // When
            createNewMovie(avengersMovie)
            val exception = shouldThrow<HttpClientResponseException> { createNewMovie(avengersMovie2) }
            // Then
            exception.message shouldBe "Movie name already exists."
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
    }

    private fun createNewMovie(avengersMovie: MovieRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/movies",
            body = jsonMapper.writeValueAsString(avengersMovie)
        )
    }

    private fun newMovieRequest(title: String, durationInMinutes: Int): MovieRequest {
        return MovieRequest(
            title,
            durationInMinutes
        )
    }
}
