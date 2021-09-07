package com.demo.book.show.request

import com.demo.book.exceptions.InvalidInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.sql.Timestamp

class ShowRequestTest : StringSpec() {
    init {
        "should return startTime in timestamp if the input format is correct" {
            val timeStampValue = ShowRequest("2021-05-05 11:02:00", 1).getStartTime()

            timeStampValue shouldBe Timestamp.valueOf("2021-05-05 11:02:00")
        }

        "should return error if the startTime input format is incorrect" {
            val exception = shouldThrow<InvalidInputException> {
                ShowRequest("2021-05-0511:02:00", 1).getStartTime()
            }

            exception.message shouldBe "start time is not in correct format"
        }
    }
}
