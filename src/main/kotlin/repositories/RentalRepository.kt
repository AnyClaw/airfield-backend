package com.example.repositories

import com.example.db.dao.PlaneDAO
import com.example.db.dao.RentalDAO
import com.example.db.dao.UserDAO
import com.example.db.suspendTransaction
import com.example.enums.RentalStatus
import com.example.models.Rental
import java.time.LocalDateTime

class RentalRepository {
    suspend fun findById(id: Int): Rental? = suspendTransaction {
        RentalDAO.findById(id)?.toModel()
    }

    suspend fun create(userId: Int, planeId: Int): Rental = suspendTransaction {
        RentalDAO.new {
            this.pilot = UserDAO[userId]
            this.plane = PlaneDAO[planeId]
            this.startTime = LocalDateTime.now()
            this.endTime = null
            this.status = RentalStatus.ACTIVE
        }.toModel()
    }
}