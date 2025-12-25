package com.example.db.dao

import com.example.db.tables.RentalFlightTable
import com.example.models.RentalFlight
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RentalFlightDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RentalFlightDAO>(RentalFlightTable)

    var rental by RentalDAO referencedOn RentalFlightTable.rental
    var route by RouteDAO referencedOn RentalFlightTable.route
    var stage by RentalFlightTable.stage

    fun toModel() = RentalFlight(
        rental.toModel(),
        route.toModel(),
        stage
    )
}