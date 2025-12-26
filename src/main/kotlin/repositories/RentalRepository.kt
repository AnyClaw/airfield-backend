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
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

class RentalRepository {
    suspend fun findAll(): List<RentalDTO> = suspendTransaction {
        RentalDAO.all()
            .map { rental ->
                val rentalFlights = RentalFlightDAO.find {
                    RentalFlightTable.rental eq rental.id
                }
                    .sortedBy { it.stage }
                    .map { it.route.toModel() }

                RentalDTO(
                    id = rental.id.value,
                    pilot = rental.pilot.toModel(),
                    plane = rental.plane.toModel(),
                    startTime = rental.startTime.toString(),
                    endTime = rental.endTime?.toString(),
                    arrivalAirport = rental.arrivalAirport.toModel(),
                    departureAirport = rental.departureAirport.toModel(),
                    currentStage = rental.currentStage,
                    route = rentalFlights,
                    isMaintenance = rental.isMaintenance,
                    maintenanceCost = rental.maintenanceCost,
                    refuelCost = rental.refuelCost,
                    status = rental.status,
                    totalCost = rental.totalCost
                )
            }
    }

    suspend fun findAllByUserId(id: Int): List<RentalDTO> = suspendTransaction {
        RentalDAO.find {
            RentalsTable.user eq id
        }
            .map { rental ->
                val rentalFlights = RentalFlightDAO.find {
                    RentalFlightTable.rental eq rental.id
                }
                    .sortedBy { it.stage }
                    .map { it.route.toModel() }

                RentalDTO(
                    id = rental.id.value,
                    pilot = rental.pilot.toModel(),
                    plane = rental.plane.toModel(),
                    startTime = rental.startTime.toString(),
                    endTime = rental.endTime?.toString(),
                    arrivalAirport = rental.arrivalAirport.toModel(),
                    departureAirport = rental.departureAirport.toModel(),
                    currentStage = rental.currentStage,
                    route = rentalFlights,
                    isMaintenance = rental.isMaintenance,
                    maintenanceCost = rental.maintenanceCost,
                    refuelCost = rental.refuelCost,
                    status = rental.status,
                    totalCost = rental.totalCost
                )
            }
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
            rental.status,
            rental.totalCost
        )
    }

    suspend fun finishFlight(id: Int): RentalDTO = suspendTransaction {
        val baseRental = RentalDAO.findByIdAndUpdate(id) {
            it.status = RentalStatus.AWAITING_PAYMENT
            it.endTime = LocalDateTime.now()
            it.totalCost = abs(Duration.between(it.endTime, it.startTime).toMillis()) +
                    it.refuelCost + it.maintenanceCost + it.plane.rentalCost.toInt()
        }!!
        val rentalFlight = RentalFlightDAO
            .find { RentalFlightTable.rental eq baseRental.id.value }
            .map { it.toModel() }
            .sortedBy { it.stage }

        val rental = rentalFlight[0].rental

        println(abs(Duration.between(rental.endTime, rental.startTime).toMillis()))
        println("${rental.refuelCost}  ${rental.maintenanceCost}  ${rental.plane.rentalCost.toInt()}")

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
            rental.status,
            rental.totalCost
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
            rental.status,
            rental.totalCost
        )
    }

    suspend fun nextStage(id: Int): RentalDTO = suspendTransaction {
        val baseRental = RentalDAO.findByIdAndUpdate(id) { it.currentStage++ }!!
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
            rental.status,
            rental.totalCost
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
            baseRental.status,
            baseRental.totalCost
        )
    }
}