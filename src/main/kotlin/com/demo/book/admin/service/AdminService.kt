package com.demo.book.admin.service

import com.demo.book.admin.exception.UsernameNotFoundException
import com.demo.book.admin.repository.AdminRepository
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminService(@Inject private val adminRepository: AdminRepository) {
    private fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(10))
    }

    private fun checkHashPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun isAuthorized(username: String, password: String): Boolean {
        return try {
            val adminCredentials = adminRepository.findAdmin(username)
            checkHashPassword(password, adminCredentials.password)
        } catch (exception: UsernameNotFoundException) {
            false
        }
    }
}
