package com.demo.book.movie.service

import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.exception.InvalidDurationException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MovieServiceTest : StringSpec() {

    private val mockMovieRepository = mockk<MovieRepository>()
    private val movieService = MovieService(mockMovieRepository)

    init {
        "should save the movie if the duration is more than 5 minutes and less than 6 hours" {
            every {
                mockMovieRepository.getMovieByTitle("ABCD")
            } throws InvalidInputException("Movie doesn't exist")

            every {
                mockMovieRepository.save(MovieRequest("ABCD", 10))
            } returns Movie(1, "ABCD", 10)

            val saveNewMovie = movieService.save(MovieRequest("ABCD", 10))

            saveNewMovie shouldBe Movie(1, "ABCD", 10)
        }

        "should return error if the duration of movie is less than 5 minutes" {
            every {
                mockMovieRepository.getMovieByTitle("ABCD")
            } throws InvalidInputException("Movie doesn't exist")

            val exception = shouldThrow<InvalidDurationException> {
                movieService.save(MovieRequest("ABCD", 4))
            }

            exception.message shouldBe "Movie duration cannot be less than 5 minutes"
        }

        "should return error if the duration of movie is more than 6 hours" {
            every {
                mockMovieRepository.getMovieByTitle("ABCD")
            } throws InvalidInputException("Movie doesn't exist")

            val exception = shouldThrow<InvalidDurationException> {
                movieService.save(MovieRequest("ABCD", 366))
            }

            exception.message shouldBe "Movie duration cannot be more than 6 hours"
        }

        "should return error if the name of movie is an empty string" {
            val exception = shouldThrow<InvalidInputException> {
                movieService.save(MovieRequest("", 300))
            }

            exception.message shouldBe "Movie name cannot be blank"
        }

        "should return error if user tries saving duplicate movie title" {
            every {
                mockMovieRepository.getMovieByTitle("ABCD")
            } returns Movie(1, "ABCD", 10)

            val exception = shouldThrow<InvalidInputException> {
                movieService.save(MovieRequest("ABCD", 361))
            }

            exception.message shouldBe "Movie name already exists."
        }
    }
}
