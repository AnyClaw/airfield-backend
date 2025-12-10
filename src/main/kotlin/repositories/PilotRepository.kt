package com.example.repositories

import com.example.db.dao.PilotDAO
import com.example.models.Pilot
import com.example.db.suspendTransaction
import com.example.db.tables.PilotTable

class PilotRepository {
    suspend fun findByUserId(id: Int): Pilot? = suspendTransaction {
        PilotDAO.find {
            PilotTable.user eq id
        }.firstOrNull()?.toModel()
    }
}