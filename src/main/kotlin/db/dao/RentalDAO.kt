package com.example.db.dao

import com.example.db.tables.RentalsTable
import com.example.models.Rental
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RentalDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RentalDAO>(RentalsTable)

    var pilot by UserDAO referencedOn RentalsTable.user
    var plane by PlaneDAO referencedOn RentalsTable.plane
    var startTime by RentalsTable.startTime
    var endTime by RentalsTable.endTime
    var arrivalAirport by AirportDAO referencedOn RentalsTable.arrivalAirport
    var departureAirport by AirportDAO referencedOn RentalsTable.departureAirport
    var isMaintenance by RentalsTable.isMaintenance
    var maintenanceCost by RentalsTable.maintenanceCost
    var refuelCost by RentalsTable.refuelCost
    var status by RentalsTable.status

    fun toModel() = Rental(
        pilot.toModel(),
        plane.toModel(),
        startTime,
        endTime,
        arrivalAirport.toModel(),
        departureAirport.toModel(),
        isMaintenance,
        maintenanceCost,
        refuelCost,
        status
    )
}