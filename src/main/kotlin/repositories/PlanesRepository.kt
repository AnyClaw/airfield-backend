package com.example.repositories

import com.example.db.dao.PlaneDAO
import com.example.db.dto.PlaneCreationDTO
import com.example.models.Plane
import com.example.db.suspendTransaction

class PlanesRepository {
    suspend fun getAllPlanes(): List<Plane> = suspendTransaction {
        PlaneDAO.all().map { it.toModel() }
    }

    suspend fun findById(id: Int): Plane? = suspendTransaction {
        PlaneDAO.findById(id)?.toModel()
    }

    suspend fun update(plane: Plane): Plane? = suspendTransaction {
        PlaneDAO.findByIdAndUpdate(plane.id) {
            it.fuel = plane.fuel
            it.rating = plane.rating
            it.condition = plane.condition
            it.mileage = plane.mileage
            it.isAvailable = plane.isAvailable
        }?.toModel()
    }

    suspend fun save(plane: PlaneCreationDTO): Plane = suspendTransaction {
        PlaneDAO.new {
            this.name = plane.name
            this.tankCapacity = plane.tankCapacity
            this.fuel = plane.fuel
            this.rentalCost = plane.rentalCost
            this.fuelConsumption = plane.fuelConsumption
            this.maintenanceCost = plane.maintenanceCost
            this.condition = plane.condition
            this.mileage = plane.mileage
        }.toModel()
    }
}