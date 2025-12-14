package com.example.services

import com.example.models.Airport
import com.example.models.Route
import com.example.models.Waypoint
import java.util.PriorityQueue
import kotlin.math.pow
import kotlin.math.sqrt

class MapService(private val waypoints: List<Waypoint>, private val routes: List<Route>) {

    fun buildFlightRoute(from: Airport, to: Airport): List<Route> = buildFlightRoute(from.waypoint, to.waypoint)

    fun buildFlightRoute(start: Waypoint, end: Waypoint): List<Route> {
        val graph = buildGraph()

        val gScore = mutableMapOf<Waypoint, Float>()
        val fScore = mutableMapOf<Waypoint, Float>()
        val cameFrom = mutableMapOf<Waypoint, Waypoint>()
        val routeMap = mutableMapOf<Pair<Waypoint, Waypoint>, Route>()
        val openSet = PriorityQueue<Waypoint>(compareBy { fScore[it] ?: Float.MAX_VALUE })

        gScore[start] = 0f
        fScore[start] = heuristic(start, end)
        openSet.add(start)

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            if (current == end) {
                return reconstructRoutePath(cameFrom, routeMap, current)
            }

            val neighbors = graph[current] ?: emptyList()

            for ((neighbor, route) in neighbors) {
                val tentativeGScore = (gScore[current] ?: Float.MAX_VALUE) + route.distance

                if (tentativeGScore < (gScore[neighbor] ?: Float.MAX_VALUE)) {
                    cameFrom[neighbor] = current
                    routeMap[Pair(current, neighbor)] = route
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + heuristic(neighbor, end)

                    if (neighbor !in openSet) {
                        openSet.add(neighbor)
                    }
                }
            }
        }

        println("Path not found from ${start.name} to ${end.name}")
        return emptyList()
    }

    private fun buildGraph(): Map<Waypoint, List<Pair<Waypoint, Route>>> {
        val waypointMap = waypoints.associateBy { it.id }
        val graph = mutableMapOf<Waypoint, MutableList<Pair<Waypoint, Route>>>()

        waypoints.forEach { point ->
            graph[point] = mutableListOf()
        }

        val routeCache = mutableMapOf<Pair<Int, Int>, Route>()

        routes.forEach { route ->
            val fromId = route.fromWaypoint.id
            val toId = route.toWaypoint.id
            routeCache[Pair(fromId, toId)] = route
            routeCache[Pair(toId, fromId)] = route
        }

        routes.forEach { route ->
            val fromWaypoint = waypointMap[route.fromWaypoint.id]
            val toWaypoint = waypointMap[route.toWaypoint.id]

            if (fromWaypoint != null && toWaypoint != null) {
                graph[fromWaypoint]?.add(toWaypoint to route)

                val reverseRoute = routeCache[Pair(toWaypoint.id, fromWaypoint.id)]
                    ?: findExistingRouteBetween(toWaypoint, fromWaypoint)

                if (reverseRoute != null) {
                    graph[toWaypoint]?.add(fromWaypoint to reverseRoute)
                }
            }
        }

        return graph
    }

    private fun findExistingRouteBetween(from: Waypoint, to: Waypoint): Route? {
        return routes.firstOrNull { route ->
            (route.fromWaypoint.id == from.id && route.toWaypoint.id == to.id) ||
                    (route.fromWaypoint.id == to.id && route.toWaypoint.id == from.id)
        }
    }

    private fun reconstructRoutePath(
        cameFrom: Map<Waypoint, Waypoint>,
        routeMap: Map<Pair<Waypoint, Waypoint>, Route>,
        end: Waypoint
    ): List<Route> {
        val routePath = mutableListOf<Route>()
        var current: Waypoint? = end

        while (current != null) {
            val previous = cameFrom[current]
            if (previous != null) {
                val route = routeMap[Pair(previous, current)]
                    ?: routeMap[Pair(current, previous)]
                    ?: findExistingRouteBetween(previous, current)

                if (route != null) {
                    routePath.add(0, route)
                } else {
                    throw IllegalStateException("No route found between ${previous.name} and ${current.name}")
                }
            }
            current = previous
        }

        return routePath
    }

    private fun heuristic(from: Waypoint, to: Waypoint): Float {
        return calculateDistance(from, to)
    }

    fun calculateDistance(point1: Waypoint, point2: Waypoint): Float {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        return sqrt(dx.pow(2) + dy.pow(2))
    }

    fun calculateRouteDistance(routeList: List<Route>): Float {
        return routeList.sumOf { it.distance.toDouble() }.toFloat()
    }

    fun extractWaypointsFromRoutes(routeList: List<Route>): List<Waypoint> {
        if (routeList.isEmpty()) return emptyList()

        val waypointList = mutableListOf<Waypoint>()
        waypointList.add(routeList.first().fromWaypoint)

        routeList.forEach { route ->
            waypointList.add(route.toWaypoint)
        }

        return waypointList
    }

    fun buildFlightRouteAsWaypoints(start: Waypoint, end: Waypoint): List<Waypoint> {
        val routeList = buildFlightRoute(start, end)
        return extractWaypointsFromRoutes(routeList)
    }
}