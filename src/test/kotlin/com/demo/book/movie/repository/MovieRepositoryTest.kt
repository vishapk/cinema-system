package com.demo.book.movie.repository

import com.demo.book.BaseIntegrationSpec
import com.demo.book.exceptions.InvalidInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import norm.executeCommand

class MovieRepositoryTest : BaseIntegrationSpec() {
    init {
        "should return error if an invalid movie id is passed" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
            }

            val exception = shouldThrow<InvalidInputException> {
                MovieRepository(dataSource).findById(2)
            }

            exception.message shouldBe "Movie id doesn't exist"
        }
    }
}
