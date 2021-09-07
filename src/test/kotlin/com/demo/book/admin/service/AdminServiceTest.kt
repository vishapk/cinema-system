package com.demo.book.admin.service

import com.demo.book.admin.entity.Admin
import com.demo.book.admin.repository.AdminRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class AdminServiceTest : StringSpec() {
    private val mockAdminRepository = mockk<AdminRepository>()
    private val adminService = AdminService(mockAdminRepository)

    init {
        "should authenticate the given user if the username and password are in the database" {
            every {
                mockAdminRepository.findAdmin("admin")
            } returns
                Admin(1, "admin", "\$2a\$10\$BGNjU0buhbrFGwiZeeZ58e5zs.T2pYaHh/20lQoXbWLGMjAULxrbm")
            val isAuthorizedUser = adminService.isAuthorized("admin", "testPassword")
            isAuthorizedUser shouldBe true
        }

        "should return false for the given user if the username and password are not in the database" {
            every {
                mockAdminRepository.findAdmin("admin")
            } returns
                Admin(1, "admin", "\$2a\$10\$BGNjU0buhbrFGwiZeeZ58e5zs.T2pYaHh/20lQoXbWLGMjAULxrbm")
            val isAuthorizedUser = adminService.isAuthorized("admin", "invalid")
            isAuthorizedUser shouldBe false
        }
    }
}
