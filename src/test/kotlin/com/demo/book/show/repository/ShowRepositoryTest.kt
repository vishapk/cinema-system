package com.demo.book.show.repository

import com.demo.book.BaseIntegrationSpec
import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.show.entity.Show
import com.demo.book.show.request.DeleteShowRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import norm.executeCommand
import java.sql.Timestamp
import java.time.LocalDateTime

class ShowRepositoryTest() : BaseIntegrationSpec() {
    init {
        "should return empty list of shows" {
            val listOfShows = ShowRepository(dataSource).findAllShows()

            listOfShows shouldBe emptyList<Show>()
        }

        "should return list of shows" {

            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-05 09:15:00', '2021-09-05 11:30:11',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2022-09-05 09:15:00', '2022-09-05 11:30:11',1);\n"
                )
            }

            val listOfShows = ShowRepository(dataSource).findAllShows()

            listOfShows shouldBe listOf<Show>(
                Show(
                    2,
                    Timestamp.valueOf("2022-09-05 09:15:00").toLocalDateTime(),
                    Timestamp.valueOf("2022-09-05 11:30:11").toLocalDateTime(),
                    1,
                    null,
                    100,
                    100
                ),
                Show(
                    1,
                    Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                    Timestamp.valueOf("2021-09-05 11:30:11").toLocalDateTime(),
                    1,
                    null,
                    100,
                    100
                )

            )
        }

        "should save a show" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
            }

            val savedShow = ShowRepository(dataSource).save(
                Timestamp.valueOf("2021-09-05 09:15:00"),
                Timestamp.valueOf("2021-09-05 11:30:11"),
                1,
                null
            )

            savedShow shouldBe Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:11").toLocalDateTime(),
                1,
                null,
                100,
                100
            )
        }

        "should return error if an invalid show id is passed" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
            }

            ShowRepository(dataSource).save(
                Timestamp.valueOf("2021-09-05 09:15:00"),
                Timestamp.valueOf("2021-09-05 11:30:11"),
                1,
                null
            )
        }

        "should return error if a movieId does not exist while creating a show" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
            }

            val exception = shouldThrow<InvalidInputException> {
                ShowRepository(dataSource).save(
                    Timestamp.valueOf("2021-09-05 09:15:00"),
                    Timestamp.valueOf("2021-09-05 11:30:11"),
                    2,
                    null
                )
            }

            exception.message shouldBe "movie id doesn't exist"
        }

        "should delete a show by id" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )

                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-04 09:15:00', '2021-09-04 11:30:11',1);\n"
                )
            }

            val listOfShows = ShowRepository(dataSource).findAllShows()

            listOfShows.size shouldBe 1

            val deletedShow = ShowRepository(dataSource).deleteShowById(DeleteShowRequest(1))

            deletedShow shouldBe Show(
                1,
                Timestamp.valueOf("2021-09-04 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-04 11:30:11").toLocalDateTime(),
                1,
                null,
                100,
                100
            )
            val listOfShowsAfterDeletion = ShowRepository(dataSource).findAllShows()

            listOfShowsAfterDeletion.size shouldBe 0
        }

        "should find show by id" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )

                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-04 09:15:00', '2021-09-04 11:30:11',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-09 09:15:00', '2021-09-09 11:30:11',1);\n"
                )
            }
            val show = ShowRepository(dataSource).findShowById(1)
            show shouldBe Show(
                1,
                Timestamp.valueOf("2021-09-04 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-04 11:30:11").toLocalDateTime(),
                1, null, 100, 100
            )
        }

        "should return error if show by id does not exist" {
            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )

                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-04 09:15:00', '2021-09-04 11:30:11',1);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id) VALUES\n" +
                        "('2021-09-09 09:15:00', '2021-09-09 11:30:11',1);\n"
                )
            }

            val exception = shouldThrow<InvalidInputException> {
                ShowRepository(dataSource).findShowById(4)
            }
            exception.message shouldBe "Show id doesn't exist"
        }

        "should return the list of past shows" {
            dataSource.connection.use { connection ->
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
                        "(current_timestamp + interval '2 day', \n" +
                        "current_timestamp + interval '2 day' + interval '2 hours',1);\n"
                )
            }

            val pastShows = ShowRepository(dataSource).findPastShows()

            pastShows shouldBe listOf<Show>(
                Show(
                    1,
                    Timestamp.valueOf("2021-08-05 09:15:00").toLocalDateTime(),
                    Timestamp.valueOf("2021-08-05 11:30:11").toLocalDateTime(),
                    1,
                    null,
                    100,
                    100
                )
            )
        }

        "should return the list of ongoing shows" {
            dataSource.connection.use { connection ->
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

            val onGoingShows = ShowRepository(dataSource).findOngoingShows()

            onGoingShows shouldBe listOf<Show>(
                Show(
                    2,
                    LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0),
                    LocalDateTime.now().withHour(23).withMinute(0).withSecond(0).withNano(0),
                    1,
                    null,
                    100,
                    100
                )
            )
        }

        "should return the list of upcoming shows" {
            dataSource.connection.use { connection ->
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

            val upcomingShows = ShowRepository(dataSource).findUpcomingShows()

            upcomingShows shouldBe listOf<Show>(
                Show(
                    1,
                    LocalDateTime.now().plusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0),
                    LocalDateTime.now().plusDays(2).withHour(1).withMinute(0).withSecond(0).withNano(0),
                    1,
                    null,
                    100,
                    100
                )
            )
        }
        "should return list of shows and available tickets" {

            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-05 09:15:00', '2021-09-05 11:30:00',1,100,100);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-06 09:15:00', '2021-09-06 11:30:00',1,100,100);\n"
                )
            }

            val listOfAvailableShows = ShowRepository(dataSource).viewAvailableTickets("Avengers EndGame")

            listOfAvailableShows shouldBe
                listOf<Show>(
                    Show(
                        1,
                        Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                        Timestamp.valueOf("2021-09-05 11:30:00").toLocalDateTime(),
                        1, null, 100, 100
                    ),
                    Show(
                        2,
                        Timestamp.valueOf("2021-09-06 09:15:00").toLocalDateTime(),
                        Timestamp.valueOf("2021-09-06 11:30:00").toLocalDateTime(),
                        1, null, 100, 100
                    )
                )
        }
        "should return list of shows and available tickets as 0 if all tickets are booked" {

            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-05 09:15:00', '2021-09-05 11:30:00',1,0,100);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-06 09:15:00', '2021-09-06 11:30:00',1,100,100);\n"
                )
            }

            val listOfAvailableShows = ShowRepository(dataSource).viewAvailableTickets("Avengers EndGame")

            listOfAvailableShows shouldBe
                listOf<Show>(
                    Show(
                        1,
                        Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                        Timestamp.valueOf("2021-09-05 11:30:00").toLocalDateTime(),
                        1, null, 100, 0
                    ),
                    Show(
                        2,
                        Timestamp.valueOf("2021-09-06 09:15:00").toLocalDateTime(),
                        Timestamp.valueOf("2021-09-06 11:30:00").toLocalDateTime(),
                        1, null, 100, 100
                    )
                )
        }
        "should return error if a movie title does not exist while viewing list of available tickets " {

            dataSource.connection.use { connection ->
                connection.executeCommand(
                    "INSERT INTO movies\n" +
                        "(title, duration_in_minutes) VALUES\n" +
                        "('Avengers EndGame', 80);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-05 09:15:00', '2021-09-05 11:30:00',1,0,100);\n"
                )
                connection.executeCommand(
                    "INSERT INTO shows\n" +
                        "(start_time, end_time,movie_id,available_tickets,capacity) VALUES\n" +
                        "('2021-09-06 09:15:00', '2021-09-01 11:30:00',1,100,100);\n"
                )
            }

            val exception = shouldThrow<InvalidInputException> {
                MovieRepository(dataSource).getMovieByTitle("Batman")
            }

            exception.message shouldBe "Movie doesn't exist"
        }
    }
}
