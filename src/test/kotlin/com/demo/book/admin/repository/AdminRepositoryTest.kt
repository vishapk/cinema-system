package com.demo.book.admin.repository

import com.demo.book.BaseIntegrationSpec
import com.demo.book.admin.exception.UsernameNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class AdminRepositoryTest() : BaseIntegrationSpec() {
    init {
        "should return admin details if username is present in the database" {
            val adminInfo = AdminRepository(dataSource).findAdmin("admin")

            adminInfo.id shouldBe 1
            adminInfo.username shouldBe "admin"
        }

        "should return error if username is not present in the database" {
            val exception = shouldThrow<UsernameNotFoundException> {
                AdminRepository(dataSource).findAdmin("notPresent")
            }

            exception.message shouldBe "Admin not found"
        }
    }
}
