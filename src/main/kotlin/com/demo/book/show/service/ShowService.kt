package com.demo.book.show.service

import com.demo.book.exceptions.InvalidOperationException
import com.demo.book.exceptions.InvalidInputException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.show.entity.Show
import com.demo.book.show.exception.ShowScheduleError
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.request.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowService(
    @Inject private val showRepository: ShowRepository,
    @Inject private val movieRepository: MovieRepository
) {
    fun save(showRequest: ShowRequest): Show {
        val movieInfo = movieRepository.findById(showRequest.movieId)
        val endTime = showRequest.getStartTime().toLocalDateTime().plusMinutes(movieInfo.durationInMinutes.toLong())
        val endTimeInTimestamp = Timestamp.valueOf(endTime)

        if (isShowBeforeCurrentTime(showRequest))
            throw ShowScheduleError("Cannot schedule a show before current time")

        if (isShowOverlapping(showRequest, endTime))
            throw ShowScheduleError(
                "Already have a show scheduled during that time"
            )

        return showRepository.save(
            showRequest.getStartTime(),
            endTimeInTimestamp,
            showRequest.movieId,
            showRequest.price
        )
    }

    private fun isShowOverlapping(
        showRequest: ShowRequest,
        endTime: LocalDateTime
    ): Boolean {
        val allShows = showRepository.findAllShows()

        allShows.map {
            if (showRequest.getStartTime().toLocalDateTime().isBefore(it.endTime) && it.startTime.isBefore(endTime))
                return true
        }
        return false
    }

    private fun isShowBeforeCurrentTime(showRequest: ShowRequest): Boolean {
        val today = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
        if (showRequest.getStartTime().toLocalDateTime().isBefore(today))
            return true
        return false
    }

    fun findAllShows(): List<Show> {
        return showRepository.findAllShows().reversed()
    }

    fun setShowPrice(showPriceRequest: ShowPriceRequest): Show {
        val shows = findShowById(showPriceRequest.showId)

        if (shows.price != null) {
            throw InvalidOperationException("Show price can not be reassigned")
        }

        return showRepository.setShowPrice(showPriceRequest)
    }

    fun viewAvailableTickets(movieTitle: String): List<Show> {
        movieRepository.getMovieByTitle(movieTitle)
        return showRepository.viewAvailableTickets(movieTitle)
    }

    private fun findShowById(showId: Int): Show {
        return showRepository.findShowById(showId)
    }

    fun deleteShowById(deleteShowRequest: DeleteShowRequest): Show {
        val showDetails = findShowById(deleteShowRequest.showId)
        if (showDetails.availableTickets != showDetails.capacity) {
            throw InvalidInputException("Show can not be cancelled")
        }
        return showRepository.deleteShowById(deleteShowRequest)
    }

    fun findShowsByStatus(status: String?): List<Show> {
        return when (status) {
            "past" -> showRepository.findPastShows()
            "ongoing" -> showRepository.findOngoingShows()
            "upcoming" -> showRepository.findUpcomingShows()
            null -> showRepository.findAllShows().reversed()
            else -> throw InvalidInputException("Invalid show status value")
        }
    }

    fun viewAvailableTicketsForShow(showWithTicketRequest: FindTicketForShowRequest): Int {

        val showDetails = findShowById(showWithTicketRequest.showId)
        return showDetails.availableTickets
    }
}
