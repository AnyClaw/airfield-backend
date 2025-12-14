package com.example.db.dao

import com.example.db.tables.WaypointTable
import com.example.models.Waypoint
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WaypointDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WaypointDAO>(WaypointTable)

    var name by WaypointTable.name
    var x by WaypointTable.x
    var y by WaypointTable.y

    fun toModel() = Waypoint(id.value, name, x, y)
}