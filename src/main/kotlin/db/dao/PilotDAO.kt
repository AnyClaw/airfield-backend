package com.example.db.dao

import com.example.db.tables.PilotTable
import com.example.models.Pilot
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PilotDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PilotDAO>(PilotTable)

    var user by UserDAO referencedOn PilotTable.user
    var license by PilotTable.license
    var mileage by PilotTable.mileage
    var balance by PilotTable.balance

    fun toModel() = Pilot(
        user.toModel(),
        license,
        mileage,
        balance.toFloat()
    )
}