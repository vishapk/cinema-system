package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.entity.Show
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.request.ShowPriceRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.service.TicketService
import com.demo.book.utils.delete
import com.demo.book.utils.get
import com.demo.book.utils.post
import com.demo.book.utils.put
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import norm.executeCommand
import java.sql.Timestamp
import java.time.LocalDateTime

class ShowApiTest : BaseIntegrationSpec() {
    init {
        "should save a show" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShow = newShowRequest("2021-09-01 11:05:00", avengersMovie.id)

            val response = createNewShow(newShow)

            response.status shouldBe HttpStatus.OK
            response.body.get() shouldBe 1
        }

        "should save multiple shows" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val firstShow = newShowRequest("2021-09-04 11:45:00", avengersMovie.id)
            val secondShow = newShowRequest("2021-09-05 10:45:00", avengersMovie.id)

            val firstShowResponse = createNewShow(firstShow)
            val secondShowResponse = createNewShow(secondShow)

            firstShowResponse.status shouldBe HttpStatus.OK
            secondShowResponse.status shouldBe HttpStatus.OK
            firstShowResponse.body.get() shouldBe 1
            secondShowResponse.body.get() shouldBe 2
        }

        "should get all saved shows in reverse chronological order" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))

            createNewShow(newShowRequest("2021-09-06 11:45:00", avengersMovie.id))
            createNewShow(newShowRequest("2021-09-05 10:45:00", avengersMovie.id))

            val response = httpClient.get<List<Show>>("/shows")

            response.status shouldBe HttpStatus.OK
            val savedShows = response.body.get()
            savedShows.size shouldBe 2

            jsonString(savedShows[1]) shouldBe """
                |{
                |  "id" : 1,
                |  "startTime" : "2021-09-06 11:45:00.000",
                |  "endTime" : "2021-09-06 13:10:00.000",
                |  "movieId" : 1,
                |  "capacity" : 100,
                |  "availableTickets" : 100
                |}
            """.trimMargin().trimIndent()
            jsonString(savedShows[0]) shouldBe """
                |{
                |  "id" : 2,
                |  "startTime" : "2021-09-05 10:45:00.000",
                |  "endTime" : "2021-09-05 12:10:00.000",
                |  "movieId" : 1,
                |  "capacity" : 100,
                |  "availableTickets" : 100
                |}
            """.trimMargin().trimIndent()
        }

        "should return empty list if no shows have been passed" {
            val response = httpClient.get<List<Show>>("/shows")

            response.status shouldBe HttpStatus.OK
            val savedShows = response.body.get()
            savedShows.size shouldBe 0
            savedShows shouldBe emptyList()
        }

        "should return error if show time overlaps with existing show" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val firstShow = newShowRequest("2021-09-05 11:45:00", avengersMovie.id)
            val secondShow = newShowRequest("2021-09-05 10:45:00", avengersMovie.id)

            createNewShow(firstShow)
            val exception = shouldThrow<HttpClientResponseException> {
                createNewShow(secondShow)
            }

            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Already have a show scheduled during that time"
        }

        "should return error if a show is scheduled before current time" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val firstShow = newShowRequest("2021-08-23 11:45:00", avengersMovie.id)

            val exception = shouldThrow<HttpClientResponseException> {
                createNewShow(firstShow)
            }

            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Cannot schedule a show before current time"
        }

        "should set price for show" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)"
                )
                connection.executeCommand(
                    "Insert into shows(start_time,end_time,movie_id) " +
                        "values ('2021-10-01 17:31:00','2021-10-01 19:46:00',1)"
                )
            }
            val show =
                Show(
                    1,
                    Timestamp.valueOf(
                        "2021-10-01 17:31:00"
                    )
                        .toLocalDateTime(),
                    Timestamp.valueOf(
                        "2021-10-01 19:46:00"
                    )
                        .toLocalDateTime(),
                    1,
                    150, 100, 100
                )

            val updatedShow =
                httpClient.put<Show, ShowPriceRequest>(
                    url = "/shows",
                    body = ShowPriceRequest(
                        1,
                        150
                    )
                )

            updatedShow.status shouldBe HttpStatus.OK
            updatedShow.body.get() shouldBe show
        }

        "should throw error for show that does Not exist" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)"
                )
                connection.executeCommand(
                    "Insert into shows(start_time,end_time,movie_id) " +
                        "values ('2021-10-01 17:31:00','2021-10-01 19:46:00',1)"
                )
            }

            val exception =
                shouldThrow<HttpClientResponseException> {
                    httpClient.put<Show, ShowPriceRequest>(
                        url = "/shows",
                        body = ShowPriceRequest(
                            5,
                            150
                        )
                    )
                }

            exception.message shouldBe "Show id doesn't exist"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should throw error for show that already has a price assigned to it." {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)"
                )
                connection.executeCommand(
                    "Insert into shows(start_time,end_time,movie_id) " +
                        "values ('2021-10-01 17:31:00','2021-10-01 19:46:00',1)"
                )
            }

            httpClient.put<Show, ShowPriceRequest>(
                url = "/shows",
                body = ShowPriceRequest(
                    1,
                    150
                )
            )

            val exception =
                shouldThrow<HttpClientResponseException> {
                    httpClient.put<Show, ShowPriceRequest>(
                        url = "/shows",
                        body = ShowPriceRequest(
                            1,
                            150
                        )
                    )
                }
            exception.message shouldBe "Show price can not be reassigned"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
        "should return error if show time is entered in incorrect format" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShow = newShowRequest("2021-09-0111:05:00", avengersMovie.id)

            val exception =
                shouldThrow<HttpClientResponseException> {
                    createNewShow(
                        newShow
                    )
                }
            exception.message shouldBe "start time is not in correct format"
        }

        "should return show specified by id with available tickets" {

            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShow = newShowRequest("2021-09-01 11:05:00", avengersMovie.id)
            val newShow2 = newShowRequest("2021-09-02 11:05:00", avengersMovie.id)
            val newShow3 = newShowRequest("2021-09-03 11:05:00", avengersMovie.id)

            createNewShow(newShow)
            createNewShow(newShow2)
            createNewShow(newShow3)

            val response = httpClient.get<Int>("/shows/2/tickets")

            response.status shouldBe HttpStatus.OK
            val ticketsAvailable = response.body.get()

            ticketsAvailable shouldBe 100
        }

        "should delete a show if no tickets have been booked" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShowCreation = newShowRequest("2021-09-01 11:05:00", avengersMovie.id)
            createNewShow(newShowCreation)

            val deletedShow =
                httpClient.delete<Int, Int?>("/shows/1", null)

            deletedShow.body.get() shouldBe 1
        }

        "should not delete a show if tickets have been booked" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShowCreation = newShowRequest("2021-09-01 11:05:00", avengersMovie.id)
            createNewShow(newShowCreation)
            ShowRepository(dataSource).setShowPrice(ShowPriceRequest(1, 150))
            TicketService(ShowRepository(dataSource)).bookTicket(1)

            val exception =
                shouldThrow<HttpClientResponseException> {
                    httpClient.delete<Int, Int?>("/shows/1", null)
                }

            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Show can not be cancelled"
        }

        "should return list of past shows" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")

                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-08-05 09:15:00', '2021-08-05 11:30:11',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_timestamp + interval '2 day',\n" +
                        "current_timestamp + interval '2 day' + interval '2 hours',1);\n"
                )
            }

            val response = httpClient.get<List<Show>>("/shows?status=past")

            response.status shouldBe HttpStatus.OK
            val savedShows = response.body.get()
            savedShows.size shouldBe 1

            jsonString(savedShows[0]) shouldBe """
                |{
                |  "id" : 1,
                |  "startTime" : "2021-08-05 09:15:00.000",
                |  "endTime" : "2021-08-05 11:30:11.000",
                |  "movieId" : 1,
                |  "capacity" : 100,
                |  "availableTickets" : 100
                |}
            """.trimMargin().trimIndent()
        }

        "should return list of ongoing shows" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")

                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_timestamp + interval '2 day',\n" +
                        "current_timestamp + interval '2 day' + interval '2 hours',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_date + interval '0 hour', current_date + interval '23 hour',1);\n"
                )
            }

            val response = httpClient.get<List<Show>>("/shows?status=ongoing")

            response.status shouldBe HttpStatus.OK
            val savedShows = response.body.get()
            savedShows.size shouldBe 1

            jsonString(savedShows[0]) shouldBe """
                |{
                |  "id" : 2,
                |  "startTime" : "${
            Timestamp.valueOf(
                LocalDateTime.now()
                    .withHour(
                        0
                    )
                    .withMinute(
                        0
                    )
                    .withSecond(
                        0
                    )
                    .withNano(
                        0
                    )
            )
            }00",
                |  "endTime" : "${
            Timestamp.valueOf(
                LocalDateTime.now()
                    .withHour(
                        23
                    )
                    .withMinute(
                        0
                    )
                    .withSecond(
                        0
                    )
                    .withNano(
                        0
                    )
            )
            }00",
                |  "movieId" : 1,
                |  "capacity" : 100,
                |  "availableTickets" : 100
                |}
            """.trimMargin().trimIndent()
        }

        "should return list of upcoming shows" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")

                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_date + interval '2 day' + interval '0 hour',\n" +
                        "current_date + interval '2 day'+ interval '1 hour',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_date + interval '0 hour', current_date + interval '23 hour',1);\n"
                )
            }

            val response = httpClient.get<List<Show>>("/shows?status=upcoming")

            response.status shouldBe HttpStatus.OK
            val savedShows = response.body.get()
            savedShows.size shouldBe 1

            jsonString(savedShows[0]) shouldBe """
                |{
                |  "id" : 1,
                |  "startTime" : "${
            Timestamp.valueOf(
                LocalDateTime.now()
                    .plusDays(
                        2
                    )
                    .withHour(
                        0
                    )
                    .withMinute(
                        0
                    )
                    .withSecond(
                        0
                    )
                    .withNano(
                        0
                    )
            )
            }00",
                |  "endTime" : "${
            Timestamp.valueOf(
                LocalDateTime.now()
                    .plusDays(
                        2
                    )
                    .withHour(
                        1
                    )
                    .withMinute(
                        0
                    )
                    .withSecond(
                        0
                    )
                    .withNano(
                        0
                    )
            )
            }00",
                |  "movieId" : 1,
                |  "capacity" : 100,
                |  "availableTickets" : 100
                |}
            """.trimMargin().trimIndent()
        }

        "should throw error if status of a show is not valid" {
            dataSource.connection.use { connection ->
                connection.executeCommand("INSERT INTO movies(title,duration_in_minutes) VALUES ('Avengers',85)")

                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-08-05 09:15:00', '2021-08-05 11:30:11',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "(current_timestamp + interval '2 day',\n" +
                        "current_timestamp + interval '2 day' + interval '2 hours',1);\n"
                )
            }

            val exception =
                shouldThrow<HttpClientResponseException> {
                    httpClient.get<List<Show>>("/shows?status=FUTURE")
                }

            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Invalid show status value"
        }

        "should throw error if show ID is not associated with any show" {

            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))
            val newShow = newShowRequest("2021-09-01 11:05:00", avengersMovie.id)
            val newShow2 = newShowRequest("2021-09-02 11:05:00", avengersMovie.id)
            val newShow3 = newShowRequest("2021-09-03 11:05:00", avengersMovie.id)

            createNewShow(newShow)
            createNewShow(newShow2)
            createNewShow(newShow3)

            val exception: Exception = shouldThrow<HttpClientResponseException> {
                httpClient.get<Show>("/shows/6/tickets")
            }

            exception.message shouldBe "Show id doesn't exist"
        }
        "should return list of shows and available tickets" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Avengers", 85))

            createNewShow(newShowRequest("2021-09-06 11:45:00", avengersMovie.id))

            val response =
                httpClient.get<List<Show>>("/movies/Avengers/shows")
            val availableTickets = response.body.get()

            response.status shouldBe HttpStatus.OK
            jsonString(availableTickets[0]) shouldBe """
               |{
               |  "id" : 1,
               |  "startTime" : "2021-09-06 11:45:00.000",
               |  "endTime" : "2021-09-06 13:10:00.000",
               |  "movieId" : 1,
               |  "capacity" : 100,
               |  "availableTickets" : 100
               |}
            """.trimMargin().trimIndent()
        }

        "should return error if movies does not exist" {
            val avengersMovie = MovieRepository(dataSource).save(MovieRequest("Singham", 85))

            createNewShow(newShowRequest("2021-09-06 11:45:00", avengersMovie.id))

            val exception = shouldThrow<HttpClientResponseException> {
                httpClient.get<List<Show>>("/movies/Batman/shows")
            }

            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Movie doesn't exist"
        }
    }

    private fun createNewShow(newShow: ShowRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/shows",
            body = """
                |{
                |    "startTime": "${newShow.startTime}",
                |    "movieId": "${newShow.movieId}"
                |}
            """.trimMargin().trimIndent()
        )
    }

    private fun newShowRequest(
        startTime: String,
        movieId: Int
    ) = ShowRequest(startTime, movieId)
}
