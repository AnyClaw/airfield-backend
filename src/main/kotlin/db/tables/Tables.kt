package com.example.db.tables

import com.example.enums.RentalStatus
import com.example.enums.UserRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object PlanesTable : IntIdTable("planes") {
    val name = varchar("name", 50)
    val tankCapacity = float("tank_capacity")
    val fuel = float("fuel")
    val isAvailable = bool("is_available")
}

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 100)
    val login = varchar("login", 50)
    val password = varchar("password", 200)
    val role = enumerationByName<UserRole>("role",20)
        .default(UserRole.PILOT)
}

object RentalsTable : IntIdTable("rentals") {
    val user = reference("id_user", UsersTable)
    val plane = reference("id_plane", PlanesTable)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time").nullable()
    val status = enumerationByName<RentalStatus>("status", 10)
}