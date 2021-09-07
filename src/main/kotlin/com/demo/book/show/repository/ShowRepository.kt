package com.demo.book.show.repository

import com.demo.book.exceptions.InvalidInputException
import com.demo.book.show.entity.Show
import com.demo.book.show.request.ShowPriceRequest
import norm.query
import show.*
import com.demo.book.show.request.DeleteShowRequest
import com.demo.book.ticket.entity.Ticket
import norm.query
import show.*
import java.sql.SQLException
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class ShowRepository(@Inject private val dataSource: DataSource) {
    fun findAllShows(): List<Show> {
        return dataSource.connection.use { connection ->
            GetAllShowsQuery().query(
                connection,
                GetAllShowsParams()
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }
    }

    fun bookTicket(showId: Int): Ticket {
        return dataSource.connection.use { connection ->
            BookTicketQuery().query(
                connection,
                BookTicketParams(showId)
            )
        }.map {
            Ticket(
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.availableTickets
            )
        }.first()
    }

    fun save(startTime: Timestamp, endTime: Timestamp?, movieId: Int, price: Int?): Show {
        try {
            return dataSource.connection.use { connection ->
                SaveShowQuery().query(
                    connection,
                    SaveShowParams(startTime, endTime, movieId, price)
                )
            }.map {
                Show(
                    it.id,
                    it.startTime.toLocalDateTime(),
                    it.endTime.toLocalDateTime(),
                    it.movieId,
                    it.price,
                    it.capacity,
                    it.availableTickets
                )
            }.first()
        } catch (exception: SQLException) {
            throw InvalidInputException("movie id doesn't exist")
        }
    }

    fun viewAvailableTickets(title: String): List<Show> {
        return dataSource.connection.use { connection ->
            AvailableTicketsQuery().query(
                connection,
                AvailableTicketsParams(title)
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }
    }

    fun findPastShows(): List<Show> {
        return dataSource.connection.use { connection ->
            GetPastShowsQuery().query(
                connection,
                GetPastShowsParams()
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }
    }

    fun findOngoingShows(): List<Show> {
        return dataSource.connection.use { connection ->
            GetOngoingShowsQuery().query(
                connection,
                GetOngoingShowsParams()
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }
    }

    fun findUpcomingShows(): List<Show> {
        return dataSource.connection.use { connection ->
            GetUpcomingShowsQuery().query(
                connection,
                GetUpcomingShowsParams()
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }
    }

    fun deleteShowById(deleteShowRequest: DeleteShowRequest): Show {
        return dataSource.connection.use { connection ->
            DeleteShowByIdQuery().query(
                connection,
                DeleteShowByIdParams(deleteShowRequest.showId)
            )
        }.map {
            Show(
                it.id,
                it.startTime.toLocalDateTime(),
                it.endTime.toLocalDateTime(),
                it.movieId,
                it.price,
                it.capacity,
                it.availableTickets
            )
        }.first()
    }

    fun setShowPrice(showPriceRequest: ShowPriceRequest): Show {
        try {
            val showWithPrice: Show =
                dataSource.connection.use { connection ->
                    SetShowPriceQuery().query(
                        connection,
                        SetShowPriceParams(showPriceRequest.price, showPriceRequest.showId)
                    )
                }.map {
                    Show(
                        it.id,
                        it.startTime.toLocalDateTime(),
                        it.endTime.toLocalDateTime(),
                        it.movieId,
                        it.price,
                        it.capacity,
                        it.availableTickets
                    )
                }.first()
            return showWithPrice
        } catch (exception: Exception) {
            throw exception
        }
    }

    fun findShowById(showId: Int): Show =
        try {
            dataSource.connection.use { connection ->
                GetShowByIdQuery().query(
                    connection,
                    GetShowByIdParams(showId)
                )
            }.map {
                Show(
                    it.id,
                    it.startTime.toLocalDateTime(),
                    it.endTime.toLocalDateTime(),
                    it.movieId,
                    it.price,
                    it.capacity,
                    it.availableTickets
                )
            }.first()
        } catch (exception: NoSuchElementException) {
            throw InvalidInputException("Show id doesn't exist")
        }
}
