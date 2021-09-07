package com.demo.book.show.entity

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class Show(
    val id: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    val startTime: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    val endTime: LocalDateTime,
    val movieId: Int,
    val price: Int?,
    val capacity: Int,
    val availableTickets: Int
)
