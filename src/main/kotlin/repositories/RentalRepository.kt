package com.example.repositories

import com.example.db.dao.AirportDAO
import com.example.db.dao.PlaneDAO
import com.example.db.dao.RentalDAO
import com.example.db.dao.RentalFlightDAO
import com.example.db.dto.RentalShortDTO
import com.example.db.dao.RouteDAO
import com.example.db.dao.UserDAO
import com.example.db.dto.RentalDTO
import com.example.db.dto.RentalQueryDTO
import com.example.db.suspendTransaction
import com.example.db.tables.RentalFlightTable
import com.example.db.tables.RentalsTable
import com.example.enums.RentalStatus
import com.example.models.Rental
import java.time.LocalDateTime

class RentalRepository {
    suspend fun findAllByUserId(id: Int): List<RentalShortDTO> = suspendTransaction {
        RentalDAO.find { RentalsTable.user eq id }.map { it.toModel().toShortDto() }
    }

    suspend fun findById(id: Int): RentalDTO = suspendTransaction {
        val rentalFlight = RentalFlightDAO
            .find { RentalFlightTable.rental eq id }
            .map { it.toModel() }
            .sortedBy { it.stage }

        val rental = rentalFlight[0].rental

        RentalDTO(
            rental.id,
            rental.pilot,
            rental.plane,
            rental.startTime.toString(),
            rental.endTime.toString(),
            rental.arrivalAirport,
            rental.departureAirport,
            rental.currentStage,
            rentalFlight.map { it.route },
            rental.isMaintenance,
            rental.maintenanceCost,
            rental.refuelCost,
            rental.status
        )
    }

    suspend fun changeStatus(id: Int, status: RentalStatus): RentalDTO = suspendTransaction {
        val baseRental = RentalDAO.findByIdAndUpdate(id) { it.status = status }!!
        val rentalFlight = RentalFlightDAO
            .find { RentalFlightTable.rental eq baseRental.id.value }
            .map { it.toModel() }
            .sortedBy { it.stage }

        val rental = rentalFlight[0].rental

        RentalDTO(
            rental.id,
            rental.pilot,
            rental.plane,
            rental.startTime.toString(),
            rental.endTime.toString(),
            rental.arrivalAirport,
            rental.departureAirport,
            rental.currentStage,
            rentalFlight.map { it.route },
            rental.isMaintenance,
            rental.maintenanceCost,
            rental.refuelCost,
            rental.status
        )
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
            baseRental.id,
            baseRental.pilot,
            baseRental.plane,
            baseRental.startTime.toString(),
            baseRental.endTime.toString(),
            baseRental.arrivalAirport,
            baseRental.departureAirport,
            baseRental.currentStage,
            rentalFlight,
            baseRental.isMaintenance,
            baseRental.maintenanceCost,
            baseRental.refuelCost,
            baseRental.status
        )
    }
}