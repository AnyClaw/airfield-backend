package com.example.repositories

import com.example.db.dao.AirportDAO
import com.example.db.dao.PlaneDAO
import com.example.db.dao.RentalDAO
import com.example.db.dao.RentalFlightDAO
import com.example.db.dao.RouteDAO
import com.example.db.dao.UserDAO
import com.example.db.dto.RentalDTO
import com.example.db.dto.RentalQueryDTO
import com.example.db.suspendTransaction
import com.example.enums.RentalStatus
import com.example.models.Rental
import java.time.LocalDateTime

class RentalRepository {
    suspend fun findById(id: Int): Rental? = suspendTransaction {
        RentalDAO.findById(id)?.toModel()
    }

    suspend fun create(rentalQueryDTO: RentalQueryDTO, ): RentalDTO = suspendTransaction {
        val rentalDAO = RentalDAO.new {
            this.pilot = UserDAO[rentalQueryDTO.userId]
            this.plane = PlaneDAO[rentalQueryDTO.planeId]
            this.startTime = LocalDateTime.now()
            this.endTime = null
            this.arrivalAirport = AirportDAO[rentalQueryDTO.arrivalAirportId]
            this.departureAirport = AirportDAO[rentalQueryDTO.departureAirportId]
            this.isMaintenance = rentalQueryDTO.maintenance
            this.maintenanceCost = rentalQueryDTO.maintenanceCost
            this.refuelCost = rentalQueryDTO.refuel * 100
            this.status = RentalStatus.ACTIVE
        }
        val baseRental = rentalDAO.toModel()

        var i = 0
        val rentalFlight = rentalQueryDTO.flightRoute.map {
            RentalFlightDAO.new {
                this.rental = rentalDAO
                this.route = RouteDAO[it.id]
                this.stage = i++
            }.toModel()
        }.sortedBy { it.stage }.map { it.route }

        RentalDTO(
            baseRental.pilot,
            baseRental.plane,
            baseRental.startTime.toString(),
            baseRental.endTime.toString(),
            baseRental.arrivalAirport,
            baseRental.departureAirport,
            rentalFlight,
            baseRental.isMaintenance,
            baseRental.maintenanceCost,
            baseRental.refuelCost,
            baseRental.status
        )
    }
}