package com.demo.book.show.request

import com.demo.book.exceptions.InvalidInputException
import java.sql.Timestamp

data class ShowRequest(
    val startTime: String,
    val movieId: Int,
    val price: Int? = null
) {
    fun getStartTime(): Timestamp {
        return try {
            Timestamp.valueOf(startTime)
        } catch (exception: IllegalArgumentException) {
            throw InvalidInputException("start time is not in correct format")
        }
    }
}
