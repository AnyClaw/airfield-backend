package com.example.repositories

import com.example.db.dao.UserDAO
import com.example.db.suspendTransaction
import com.example.db.tables.UsersTable
import com.example.db.dto.UserRegisterDTO
import com.example.models.User

class UsersRepository {
    suspend fun findById(id: Int): User? = suspendTransaction {
        UserDAO.findById(id)
            ?.let { User(it.id.value, it.username, it.login, it.role) }
    }

    suspend fun findAuthData(login: String): Pair<User, String>? = suspendTransaction {
        UserDAO.find { UsersTable.login eq login }.firstOrNull()
            ?.let { dao -> Pair(dao.toModel(), dao.password) }
    }

    suspend fun createUser(user: UserRegisterDTO) = suspendTransaction {
        UserDAO.new {
            this.username = user.username
            this.login = user.login
            this.password = user.password
        }
        return@suspendTransaction
    }
}