package com.example.db.tables

import com.example.enums.Condition
import com.example.enums.RentalStatus
import com.example.enums.UserRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object WaypointTable : IntIdTable("waypoint") {
    val name = varchar("name", 20)
    val x = float("x")
    val y = float("y")
}

object AirportTable : IntIdTable("airport") {
    val name = varchar("name", 20)
    val icao = varchar("icao", 5)
    val waypoint = reference("id_waypoint", WaypointTable)
}

object RouteTable : IntIdTable("route") {
    val fromWaypoint = reference("id_from_waypoint", WaypointTable)
    val toWaypoint = reference("id_to_waypoint", WaypointTable)
    val distance = float("distance")
}

object PlanesTable : IntIdTable("planes") {
    val name = varchar("name", 50)
    val tankCapacity = float("tank_capacity")
    val fuel = float("fuel")
    val isAvailable = bool("is_available")
    val rating = float("rating")
    val rentalCost = double("rental_cost")
    val fuelConsumption = float("fuel_consumption")
    val maintenanceCost = float("maintenance_cost")
    val condition = enumerationByName<Condition>("condition", 20)
    val mileage = float("mileage")
}

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 100)
    val login = varchar("login", 50)
    val password = varchar("password", 200)
    val role = enumerationByName<UserRole>("role",20)
        .default(UserRole.PILOT)
}

object PilotTable : IntIdTable("pilot") {
    val user = reference("id_user", UsersTable)
    val license = varchar("license", 50)
    val mileage = float("mileage")
    val balance = decimal("balance", 10, 2)
}

object RentalsTable : IntIdTable("rentals") {
    val user = reference("id_user", UsersTable)
    val plane = reference("id_plane", PlanesTable)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time").nullable()
    val arrivalAirport = reference("id_arrival_airport", AirportTable)
    val departureAirport = reference("id_departure_airport", AirportTable)
    val currentStage = integer("current_stage").default(0)
    val isMaintenance = bool("is_maintenance")
    val maintenanceCost = integer("maintenance_cost")
    val refuelCost = integer("refuel_cost")
    val status = enumerationByName<RentalStatus>("status", 10).default(RentalStatus.RENTED)
}

object RentalFlightTable : IntIdTable("rental_flight") {
    val rental = reference("id_rental", RentalsTable)
    val route = reference("id_route", RouteTable)
    val stage = integer("stage")
}