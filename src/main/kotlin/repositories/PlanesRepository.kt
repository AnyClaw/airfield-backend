package com.example.repositories

import com.example.db.dao.PlaneDAO
import com.example.models.Plane
import com.example.db.suspendTransaction

class PlanesRepository {
    suspend fun getAllPlanes(): List<Plane> = suspendTransaction {
        PlaneDAO.all().map { it.toModel() }
    }

    suspend fun findById(id: Int): Plane? = suspendTransaction {
        PlaneDAO.findById(id)?.toModel()
    }

    suspend fun save(plane: Plane): Plane? = suspendTransaction {
        PlaneDAO.findByIdAndUpdate(plane.id) {
            it.isAvailable = plane.isAvailable
        }?.toModel()
    }
}