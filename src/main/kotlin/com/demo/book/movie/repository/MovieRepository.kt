package com.demo.book.movie.repository

import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.request.MovieRequest
import movie.*
import norm.query
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class MovieRepository(@Inject private val datasource: DataSource) {

    fun save(movieToSave: MovieRequest): Movie {
        return datasource.connection.use { connection ->
            SaveMovieQuery().query(
                connection,
                SaveMovieParams(
                    movieToSave.title,
                    movieToSave.durationInMinutes
                )
            )
        }.map {
            Movie(
                it.id,
                it.title,
                it.durationInMinutes
            )
        }.first()
    }

    fun findAll(): List<Movie> = datasource.connection.use { connection ->
        GetAllMoviesQuery().query(
            connection,
            GetAllMoviesParams()
        )
    }.map {
        Movie(
            it.id,
            it.title,
            it.durationInMinutes
        )
    }

    fun findById(movieId: Int): Movie =
        try {
            datasource.connection.use { connection ->
                GetMovieByIdQuery().query(
                    connection,
                    GetMovieByIdParams(movieId)
                )
            }.map {
                Movie(
                    it.id,
                    it.title,
                    it.durationInMinutes
                )
            }.first()
        } catch (exception: NoSuchElementException) {
            throw InvalidInputException("Movie id doesn't exist")
        }

    fun getMovieByTitle(title: String): Movie {
        try {
            return datasource.connection.use { connection ->
                GetMovieByNameQuery().query(
                    connection,
                    GetMovieByNameParams(title)
                )
            }.map {
                Movie(
                    it.id,
                    it.title,
                    it.durationInMinutes
                )
            }.first()
        } catch (exception: NoSuchElementException) {
            throw InvalidInputException("Movie doesn't exist")
        }
    }
}
