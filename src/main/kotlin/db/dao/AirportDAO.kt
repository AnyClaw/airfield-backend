package com.example.db.dao

import com.example.db.tables.AirportTable
import com.example.models.Airport
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AirportDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AirportDAO>(AirportTable)

    val name by AirportTable.name
    val icao by AirportTable.icao
    val waypoint by WaypointDAO referencedOn AirportTable.waypoint

    fun toModel() = Airport(id.value, name, icao, waypoint.toModel())
}