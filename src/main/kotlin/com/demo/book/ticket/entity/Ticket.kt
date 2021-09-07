package com.demo.book.ticket.entity

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class Ticket(
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    val startTime: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    val endTime: LocalDateTime,
    val ticketNo: Int
)
