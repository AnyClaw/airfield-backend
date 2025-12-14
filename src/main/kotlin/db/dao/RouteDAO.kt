package com.example.db.dao

import com.example.db.tables.RouteTable
import com.example.models.Route
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RouteDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RouteDAO>(RouteTable)

    val fromWaypoint by WaypointDAO referencedOn RouteTable.fromWaypoint
    val toWaypoint by WaypointDAO referencedOn RouteTable.toWaypoint
    val distance by RouteTable.distance

    fun toModel() = Route(
        id.value,
        fromWaypoint.toModel(),
        toWaypoint.toModel(),
        distance)
}