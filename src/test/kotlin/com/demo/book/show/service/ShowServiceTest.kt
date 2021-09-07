package com.demo.book.show.service

import com.demo.book.exceptions.InvalidOperationException
import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.show.entity.Show
import com.demo.book.show.exception.ShowScheduleError
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.request.ShowPriceRequest
import com.demo.book.show.request.DeleteShowRequest
import com.demo.book.show.request.FindTicketForShowRequest
import com.demo.book.show.request.ShowRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.sql.Timestamp

class ShowServiceTest : StringSpec() {

    private val mockShowRepository = mockk<ShowRepository>()
    private val mockMovieRepository = mockk<MovieRepository>()
    private val showService = ShowService(mockShowRepository, mockMovieRepository)

    init {

        "should not save a show for a past date" {
            every {
                mockMovieRepository.findById(1)
            } returns Movie(1, "ABCD", 135)

            every {
                mockShowRepository.findAllShows()
            } returns emptyList<Show>()

            val exception = shouldThrow<ShowScheduleError> {
                showService.save(ShowRequest("2020-09-05 09:09:09", 1))
            }
            exception.message shouldBe "Cannot schedule a show before current time"
        }

        "should save a show for a future date" {

            val show1 =
                Show(
                    1,
                    Timestamp.valueOf(
                        "2021-09-05 09:15:00"
                    )
                        .toLocalDateTime(),
                    Timestamp.valueOf(
                        "2021-09-05 11:30:00"
                    )
                        .toLocalDateTime(),
                    1,
                    null,
                    100,
                    100
                )

            every {
                mockMovieRepository.findById(1)
            } returns Movie(1, "ABCD", 135)

            every {
                mockShowRepository.findAllShows()
            } returns emptyList<Show>()

            every {
                mockShowRepository.save(
                    Timestamp.valueOf("2021-09-05 09:15:00"),
                    Timestamp.valueOf("2021-09-05 11:30:00"),
                    1,
                    null
                )
            } returns show1

            showService.save(ShowRequest("2021-09-05 09:15:00", 1)) shouldBe show1
        }

        "should not save a show for a overlapping date-time" {

            val show1 = Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:00").toLocalDateTime(),
                1, null, 100, 100
            )
            val show2 = Show(
                2,
                Timestamp.valueOf("2021-09-06 03:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-06 05:30:00").toLocalDateTime(),
                1, null, 100, 100
            )
            val listOfShows = listOf<Show>(show1, show2)

            every {
                mockMovieRepository.findById(1)
            } returns Movie(1, "ABCD", 135)

            every {
                mockShowRepository.findAllShows()
            } returns listOfShows

            val exception = shouldThrow<ShowScheduleError> {
                showService.save(ShowRequest("2021-09-05 09:15:00", 1))
            }
            exception.message shouldBe "Already have a show scheduled during that time"
        }

        "should return list of shows with available tickets" {

            val availableShow1 = Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:11").toLocalDateTime(),
                1,
                null,
                100,
                100
            )
            val availableShow2 = Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:11").toLocalDateTime(),
                1,
                null,
                100,
                100
            )
            val listOfShows = listOf<Show>(availableShow1, availableShow2)

            every {
                mockMovieRepository.getMovieByTitle("Avengers")
            } returns Movie(1, "Avengers", 135)

            every {
                mockShowRepository.viewAvailableTickets("Avengers")
            } returns listOfShows

            showService.viewAvailableTickets("Avengers") shouldBe listOfShows
        }

        "should set price for a given show" {
            val show =
                Show(
                    1,
                    Timestamp.valueOf(
                        "2021-09-06 03:15:00"
                    )
                        .toLocalDateTime(),
                    Timestamp.valueOf(
                        "2021-09-06 05:30:00"
                    )
                        .toLocalDateTime(),
                    1,
                    null,
                    100,
                    100
                )
            val showPriceRequest =
                ShowPriceRequest(
                    1,
                    150
                )
            every {
                mockShowRepository.findShowById(
                    1
                )
            } returns show
            every {
                mockShowRepository.setShowPrice(
                    showPriceRequest
                )
            } returns show.copy(
                price = 100
            )

            showService.setShowPrice(
                showPriceRequest
            ) shouldBe Show(
                1,
                Timestamp.valueOf(
                    "2021-09-06 03:15:00"
                )
                    .toLocalDateTime(),
                Timestamp.valueOf(
                    "2021-09-06 05:30:00"
                )
                    .toLocalDateTime(),
                1,
                100,
                100,
                100
            )
        }

        "should throw an error when show already has price" {
            val show =
                Show(
                    2,
                    Timestamp.valueOf(
                        "2021-09-06 03:15:00"
                    )
                        .toLocalDateTime(),
                    Timestamp.valueOf(
                        "2021-09-06 05:30:00"
                    )
                        .toLocalDateTime(),
                    1,
                    150,
                    100,
                    100
                )
            every {
                mockShowRepository.findShowById(
                    2
                )
            } returns show
            val showPriceRequest =
                ShowPriceRequest(
                    2,
                    150
                )
            val exception =
                shouldThrow<InvalidOperationException> {
                    showService.setShowPrice(
                        showPriceRequest
                    )
                }
            exception.message shouldBe "Show price can not be reassigned"
        }
        "should delete the show if no tickets are sold" {
            val show1 = Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:00").toLocalDateTime(),
                1, null, 100, 100
            )
            every {
                mockShowRepository.findShowById(1)
            } returns show1

            every {
                mockShowRepository.deleteShowById(DeleteShowRequest(1))
            } returns show1

            val deletedMovie = showService.deleteShowById(DeleteShowRequest(1))

            deletedMovie shouldBe show1
        }

        "Should not delete the show if tickets are already sold" {
            val show1 = Show(
                1,
                Timestamp.valueOf("2021-09-05 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-05 11:30:00").toLocalDateTime(),
                1, null, 100, 96
            )
            every {
                mockShowRepository.findShowById(1)
            } returns show1
            val exception = shouldThrow<InvalidInputException> {
                showService.deleteShowById(DeleteShowRequest(show1.id))
            }

            exception.message shouldBe "Show can not be cancelled"
        }

        "should return a show with available tickets if show exists" {

            val requiredShow = Show(
                1,
                Timestamp.valueOf("2021-09-06 09:15:00").toLocalDateTime(),
                Timestamp.valueOf("2021-09-06 11:30:00").toLocalDateTime(),
                1,
                null,
                100,
                44
            )

            every {
                mockShowRepository.findShowById(1)
            } returns requiredShow

            showService.viewAvailableTicketsForShow(FindTicketForShowRequest(1)) shouldBe requiredShow.availableTickets
        }

        "should return show by status if the status is valid" {
            every {
                mockShowRepository.findPastShows()
            } returns listOf(
                Show(
                    1,
                    Timestamp.valueOf("2021-07-05 09:15:00").toLocalDateTime(),
                    Timestamp.valueOf("2021-07-05 11:30:00").toLocalDateTime(),
                    1, null, 100, 100
                )
            )

            val showByStatus = showService.findShowsByStatus("past")

            showByStatus shouldBe listOf(
                Show(
                    1,
                    Timestamp.valueOf("2021-07-05 09:15:00").toLocalDateTime(),
                    Timestamp.valueOf("2021-07-05 11:30:00").toLocalDateTime(),
                    1, null, 100, 100
                )
            )
        }

        "should return error if status of a show is not valid" {
            val exception = shouldThrow<InvalidInputException> {
                showService.findShowsByStatus("future")
            }

            exception.message shouldBe "Invalid show status value"
        }
    }
}
