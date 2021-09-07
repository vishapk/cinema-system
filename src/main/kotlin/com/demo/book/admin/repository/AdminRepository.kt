package com.demo.book.admin.repository

import admin.GetAdminCredentialsParams
import admin.GetAdminCredentialsQuery
import com.demo.book.admin.entity.Admin
import com.demo.book.admin.exception.UsernameNotFoundException
import norm.query
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class AdminRepository(@Inject private val dataSource: DataSource) {
    fun findAdmin(username: String): Admin {
        return try {
            dataSource.connection.use { connection ->
                GetAdminCredentialsQuery().query(
                    connection,
                    GetAdminCredentialsParams(username)
                )
            }.map {
                Admin(
                    it.id,
                    it.username,
                    it.password
                )
            }.first()
        } catch (exception: Exception) {
            throw UsernameNotFoundException("Admin not found")
        }
    }
}
