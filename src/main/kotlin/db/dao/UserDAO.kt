package com.example.db.dao

import com.example.db.tables.UsersTable
import com.example.models.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UsersTable)

    var username by UsersTable.username
    var login by UsersTable.login
    var password by UsersTable.password
    var role by UsersTable.role

    fun toModel() = User(id.value, username, login, role)
}