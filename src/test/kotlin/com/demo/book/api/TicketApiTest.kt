package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.request.ShowPriceRequest
import com.demo.book.utils.post
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import norm.executeCommand
import java.sql.Timestamp
import java.time.LocalDateTime

class TicketApiTest : BaseIntegrationSpec() {
    init {
        "should not book a ticket if no shows are available" {

            val exception = shouldThrow<HttpClientResponseException> { bookTicket(1) }
            exception.message shouldBe "No Shows are available"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should book a ticket" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(15)
                    )
                    }','${Timestamp.valueOf(LocalDateTime.now().plusMinutes(100))}',1)"
                ) // ktlint-disable max-line-length
            }
            ShowRepository(dataSource).setShowPrice(ShowPriceRequest(1, 150))
            val response = bookTicket(1)

            response.status shouldBe HttpStatus.OK
            response.body.get() shouldBe 100
        }

        "should not book a ticket if no tickets are available" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(15)
                    )
                    }','${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(100)
                    )
                    }',1,100,0)" // ktlint-disable max-line-length
                )
            }
            ShowRepository(dataSource).setShowPrice(ShowPriceRequest(1, 150))

            val exception = shouldThrow<HttpClientResponseException> { bookTicket(1) }

            exception.message shouldBe "No tickets available"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should not book a ticket if date is previous from today" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .minusDays(1)
                    )
                    }','${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .minusDays(1)
                            .plusMinutes(85)
                    )
                    }',1,100,100)" // ktlint-disable max-line-length
                )
            }
            ShowRepository(dataSource).setShowPrice(ShowPriceRequest(1, 150))

            val exception = shouldThrow<HttpClientResponseException> { bookTicket(1) }

            exception.message shouldBe "Cannot book ticket for show before today's date"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should not book a ticket if date is after 7 days" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusDays(8)
                    )
                    }','${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusDays(8)
                    )
                    }',1,100,100)" // ktlint-disable max-line-length
                )
            }
            ShowRepository(dataSource).setShowPrice(ShowPriceRequest(1, 150))

            val exception = shouldThrow<HttpClientResponseException> { bookTicket(1) }

            exception.message shouldBe "Cannot book ticket for show beyond 7 days"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should not book a ticket if invalid show number" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(15)
                    )
                    }','${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(100)
                    )
                    }',1,100,100)" // ktlint-disable max-line-length
                ) // ktlint-disable max-line-length
            }

            val exception = shouldThrow<HttpClientResponseException> { bookTicket(2) }

            exception.message shouldBe "No Shows are available"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should not book a ticket if price is not assigned to the show" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")
                connection.executeCommand(
                    "INSERT INTO shows values(1,'${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(15)
                    )
                    }','${
                    Timestamp.valueOf(
                        LocalDateTime.now()
                            .plusMinutes(100)
                    )
                    }',1,100,0)" // ktlint-disable max-line-length
                )
            }
            val exception = shouldThrow<HttpClientResponseException> { bookTicket(1) }

            exception.message shouldBe "Show price is not available"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
    }

    private fun bookTicket(showId: Int): HttpResponse<Any> {
        return httpClient.post(
            url = "/show/$showId/ticket",
            body = null
        )
    }
}
