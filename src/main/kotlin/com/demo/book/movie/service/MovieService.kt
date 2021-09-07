package com.demo.book.movie.service

import com.demo.book.movie.entity.Movie
import com.demo.book.movie.exception.InvalidDurationException
import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieService(@Inject val movieRepository: MovieRepository) {

    fun save(movieRequest: MovieRequest): Movie {
        if (movieRequest.title == "")
            throw InvalidInputException("Movie name cannot be blank")
        if (getMovieByTitle(movieRequest.title) != null)
            throw InvalidInputException("Movie name already exists.")
        if (movieRequest.durationInMinutes < 5)
            throw InvalidDurationException("Movie duration cannot be less than 5 minutes")
        else if (movieRequest.durationInMinutes > 360)
            throw InvalidDurationException("Movie duration cannot be more than 6 hours")

        return movieRepository.save(movieRequest)
    }

    fun allMovies(): List<Movie> {
        return movieRepository.findAll()
    }

    private fun getMovieByTitle(title: String): Movie? {
        return try {
            movieRepository.getMovieByTitle(title)
        } catch (exception: InvalidInputException) {
            return null
        }
    }
}
