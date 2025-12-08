package com.example.db.dao

import com.example.db.tables.PlanesTable
import com.example.enums.Condition
import com.example.models.Plane
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PlaneDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PlaneDAO>(PlanesTable)

    var name by PlanesTable.name
    var tankCapacity by PlanesTable.tankCapacity
    var fuel by PlanesTable.fuel
    var isAvailable by PlanesTable.isAvailable
    var rating by PlanesTable.rating
    var rentalCost by PlanesTable.rentalCost
    var fuelConsumption by PlanesTable.fuelConsumption
    var maintenanceCost by PlanesTable.maintenanceCost
    var condition by PlanesTable.condition
    var mileage by PlanesTable.mileage

    fun toModel() = Plane(
        id.value,
        name,
        tankCapacity,
        fuel,
        isAvailable,
        rating,
        rentalCost,
        fuelConsumption,
        maintenanceCost,
        condition,
        mileage
    )
}