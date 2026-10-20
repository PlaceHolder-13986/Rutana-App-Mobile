package com.example.rutana.data.planning

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlanningDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    companion object {
        private const val DB_NAME = "rutana_planning.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE routes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT,
                name TEXT,
                type TEXT,
                color_tag TEXT,
                date TEXT,
                status TEXT,
                vehicle_team_id INTEGER,
                vehicle_plate TEXT,
                driver_name TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE route_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                route_id INTEGER,
                location_id INTEGER,
                client_name TEXT,
                location_name TEXT,
                address TEXT,
                lat REAL,
                lng REAL,
                status TEXT,
                rejection_reason TEXT,
                confirm_time TEXT,
                FOREIGN KEY(route_id) REFERENCES routes(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE available_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_name TEXT,
                location_name TEXT,
                address TEXT,
                lat REAL,
                lng REAL,
                is_reserved INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE available_teams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                vehicle_plate TEXT,
                capacity_kg REAL,
                driver_name TEXT,
                members TEXT,
                is_available INTEGER DEFAULT 1
            )
            """.trimIndent()
        )

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS route_locations")
        db.execSQL("DROP TABLE IF EXISTS routes")
        db.execSQL("DROP TABLE IF EXISTS available_locations")
        db.execSQL("DROP TABLE IF EXISTS available_teams")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Seed Available Locations (CRM Mock)
        val locations = listOf(
            ContentValues().apply {
                put("client_name", "ACME Corp")
                put("location_name", "Main Warehouse")
                put("address", "Av. Industrial 123, Lima")
                put("lat", -12.046374)
                put("lng", -77.042793)
                put("is_reserved", 0)
            },
            ContentValues().apply {
                put("client_name", "ACME Corp")
                put("location_name", "North Branch")
                put("address", "Av. Los Olivos 456, Lima")
                put("lat", -11.992381)
                put("lng", -77.070123)
                put("is_reserved", 0)
            },
            ContentValues().apply {
                put("client_name", "Distribuidora Centro")
                put("location_name", "Sede Central")
                put("address", "Av. Primavera 789, Surco")
                put("lat", -12.112345)
                put("lng", -76.987654)
                put("is_reserved", 0)
            },
            ContentValues().apply {
                put("client_name", "Logística Peruana")
                put("location_name", "Almacén Callao")
                put("address", "Av. Faucett 1010, Callao")
                put("lat", -12.023456)
                put("lng", -77.112233)
                put("is_reserved", 0)
            }
        )
        locations.forEach { db.insert("available_locations", null, it) }

        // Seed Available Teams (Fleet Mock)
        val teams = listOf(
            ContentValues().apply {
                put("vehicle_plate", "ABC-123")
                put("capacity_kg", 1500.0)
                put("driver_name", "Juan Pérez")
                put("members", "Juan Pérez, Pedro Gómez")
                put("is_available", 1)
            },
            ContentValues().apply {
                put("vehicle_plate", "DEF-456")
                put("capacity_kg", 2500.0)
                put("driver_name", "Carlos Ruiz")
                put("members", "Carlos Ruiz, Mario Silva")
                put("is_available", 1)
            },
            ContentValues().apply {
                put("vehicle_plate", "GHI-789")
                put("capacity_kg", 3500.0)
                put("driver_name", "Luis Morales")
                put("members", "Luis Morales, Ana Torres")
                put("is_available", 1)
            }
        )
        teams.forEach { db.insert("available_teams", null, it) }

        // Seed Sample Route 1
        val routeValues1 = ContentValues().apply {
            put("code", "R-2025-001")
            put("name", "Ruta Distribución Lima Norte")
            put("type", "Distribución Regular")
            put("color_tag", "#3B82F6")
            put("date", todayStr)
            put("status", RouteStatus.DRAFT.name)
            put("vehicle_team_id", 1)
            put("vehicle_plate", "ABC-123")
            put("driver_name", "Juan Pérez")
        }
        val routeId1 = db.insert("routes", null, routeValues1)

        val loc1 = ContentValues().apply {
            put("route_id", routeId1)
            put("location_id", 1)
            put("client_name", "ACME Corp")
            put("location_name", "Main Warehouse")
            put("address", "Av. Industrial 123, Lima")
            put("lat", -12.046374)
            put("lng", -77.042793)
            put("status", LocationDeliveryStatus.PENDING.name)
        }
        val loc2 = ContentValues().apply {
            put("route_id", routeId1)
            put("location_id", 2)
            put("client_name", "ACME Corp")
            put("location_name", "North Branch")
            put("address", "Av. Los Olivos 456, Lima")
            put("lat", -11.992381)
            put("lng", -77.070123)
            put("status", LocationDeliveryStatus.PENDING.name)
        }
        db.insert("route_locations", null, loc1)
        db.insert("route_locations", null, loc2)

        // Seed Sample Route 2 (Published)
        val routeValues2 = ContentValues().apply {
            put("code", "R-2025-002")
            put("name", "Ruta Express Surco")
            put("type", "Entrega Express")
            put("color_tag", "#10B981")
            put("date", todayStr)
            put("status", RouteStatus.PUBLISHED.name)
            put("vehicle_team_id", 2)
            put("vehicle_plate", "DEF-456")
            put("driver_name", "Carlos Ruiz")
        }
        val routeId2 = db.insert("routes", null, routeValues2)

        val loc3 = ContentValues().apply {
            put("route_id", routeId2)
            put("location_id", 3)
            put("client_name", "Distribuidora Centro")
            put("location_name", "Sede Central")
            put("address", "Av. Primavera 789, Surco")
            put("lat", -12.112345)
            put("lng", -76.987654)
            put("status", LocationDeliveryStatus.COMPLETED.name)
            put("confirm_time", "09:30 AM")
        }
        val loc4 = ContentValues().apply {
            put("route_id", routeId2)
            put("location_id", 4)
            put("client_name", "Logística Peruana")
            put("location_name", "Almacén Callao")
            put("address", "Av. Faucett 1010, Callao")
            put("lat", -12.023456)
            put("lng", -77.112233)
            put("status", LocationDeliveryStatus.PENDING.name)
        }
        db.insert("route_locations", null, loc3)
        db.insert("route_locations", null, loc4)
    }

    fun getAllRoutes(dateFilter: String? = null): List<Route> {
        val db = readableDatabase
        val routes = mutableListOf<Route>()

        val selection = if (dateFilter.isNull_or_blank()) null else "date = ?"
        val selectionArgs = if (dateFilter.isNull_or_blank()) null else arrayOf(dateFilter)

        val cursor = db.query(
            "routes",
            null,
            selection,
            selectionArgs,
            null,
            null,
            "id DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                val colorTag = cursor.getString(cursor.getColumnIndexOrThrow("color_tag"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val vehicleTeamId = if (cursor.isNull(cursor.getColumnIndexOrThrow("vehicle_team_id"))) null else cursor.getLong(cursor.getColumnIndexOrThrow("vehicle_team_id"))
                val vehiclePlate = cursor.getString(cursor.getColumnIndexOrThrow("vehicle_plate"))
                val driverName = cursor.getString(cursor.getColumnIndexOrThrow("driver_name"))

                val status = try { RouteStatus.valueOf(statusStr) } catch (e: Exception) { RouteStatus.DRAFT }

                // Count locations
                val locCountCursor = db.rawQuery("SELECT COUNT(*), SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) FROM route_locations WHERE route_id = ?", arrayOf(id.toString()))
                var locCount = 0
                var completedCount = 0
                if (locCountCursor.moveToFirst()) {
                    locCount = locCountCursor.getInt(0)
                    completedCount = locCountCursor.getInt(1)
                }
                locCountCursor.close()

                routes.add(
                    Route(
                        id = id,
                        code = code,
                        name = name,
                        type = type,
                        colorTag = colorTag,
                        date = date,
                        status = status,
                        vehicleTeamId = vehicleTeamId,
                        vehiclePlate = vehiclePlate,
                        driverName = driverName,
                        locationCount = locCount,
                        completedCount = completedCount
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return routes
    }

    fun getRouteById(id: Long): Route? {
        val db = readableDatabase
        val cursor = db.query("routes", null, "id = ?", arrayOf(id.toString()), null, null, null)
        var route: Route? = null
        if (cursor.moveToFirst()) {
            val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
            val colorTag = cursor.getString(cursor.getColumnIndexOrThrow("color_tag"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            val vehicleTeamId = if (cursor.isNull(cursor.getColumnIndexOrThrow("vehicle_team_id"))) null else cursor.getLong(cursor.getColumnIndexOrThrow("vehicle_team_id"))
            val vehiclePlate = cursor.getString(cursor.getColumnIndexOrThrow("vehicle_plate"))
            val driverName = cursor.getString(cursor.getColumnIndexOrThrow("driver_name"))
            val status = try { RouteStatus.valueOf(statusStr) } catch (e: Exception) { RouteStatus.DRAFT }

            val locCountCursor = db.rawQuery("SELECT COUNT(*), SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) FROM route_locations WHERE route_id = ?", arrayOf(id.toString()))
            var locCount = 0
            var completedCount = 0
            if (locCountCursor.moveToFirst()) {
                locCount = locCountCursor.getInt(0)
                completedCount = locCountCursor.getInt(1)
            }
            locCountCursor.close()

            route = Route(
                id = id,
                code = code,
                name = name,
                type = type,
                colorTag = colorTag,
                date = date,
                status = status,
                vehicleTeamId = vehicleTeamId,
                vehiclePlate = vehiclePlate,
                driverName = driverName,
                locationCount = locCount,
                completedCount = completedCount
            )
        }
        cursor.close()
        return route
    }

    fun createDraftRoute(name: String, type: String, colorTag: String, date: String): Long {
        val db = writableDatabase
        val routeCode = "R-${System.currentTimeMillis() % 10000}"
        val values = ContentValues().apply {
            put("code", routeCode)
            put("name", name)
            put("type", type)
            put("color_tag", colorTag)
            put("date", date)
            put("status", RouteStatus.DRAFT.name)
        }
        return db.insert("routes", null, values)
    }

    fun getAvailableLocations(): List<AvailableLocation> {
        val db = readableDatabase
        val list = mutableListOf<AvailableLocation>()
        val cursor = db.query("available_locations", null, null, null, null, null, "client_name ASC")
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val clientName = cursor.getString(cursor.getColumnIndexOrThrow("client_name"))
                val locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"))
                val lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"))
                val isReserved = cursor.getInt(cursor.getColumnIndexOrThrow("is_reserved")) == 1

                list.add(
                    AvailableLocation(
                        id = id,
                        clientName = clientName,
                        locationName = locationName,
                        address = address,
                        lat = lat,
                        lng = lng,
                        isReservedToday = isReserved
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getRouteLocations(routeId: Long): List<RouteLocationItem> {
        val db = readableDatabase
        val list = mutableListOf<RouteLocationItem>()
        val cursor = db.query("route_locations", null, "route_id = ?", arrayOf(routeId.toString()), null, null, "id ASC")
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val locationId = cursor.getLong(cursor.getColumnIndexOrThrow("location_id"))
                val clientName = cursor.getString(cursor.getColumnIndexOrThrow("client_name"))
                val locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"))
                val lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"))
                val statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val rejectionReason = cursor.getString(cursor.getColumnIndexOrThrow("rejection_reason"))
                val confirmTime = cursor.getString(cursor.getColumnIndexOrThrow("confirm_time"))

                val status = try { LocationDeliveryStatus.valueOf(statusStr) } catch (e: Exception) { LocationDeliveryStatus.PENDING }

                list.add(
                    RouteLocationItem(
                        id = id,
                        routeId = routeId,
                        locationId = locationId,
                        clientName = clientName,
                        locationName = locationName,
                        address = address,
                        lat = lat,
                        lng = lng,
                        status = status,
                        rejectionReason = rejectionReason,
                        confirmTime = confirmTime
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun addLocationToRoute(routeId: Long, location: AvailableLocation): Boolean {
        val db = writableDatabase
        // Check duplicate
        val checkCursor = db.query(
            "route_locations",
            null,
            "route_id = ? AND location_id = ?",
            arrayOf(routeId.toString(), location.id.toString()),
            null, null, null
        )
        if (checkCursor.count > 0) {
            checkCursor.close()
            return false
        }
        checkCursor.close()

        val values = ContentValues().apply {
            put("route_id", routeId)
            put("location_id", location.id)
            put("client_name", location.clientName)
            put("location_name", location.locationName)
            put("address", location.address)
            put("lat", location.lat)
            put("lng", location.lng)
            put("status", LocationDeliveryStatus.PENDING.name)
        }
        return db.insert("route_locations", null, values) > 0
    }

    fun removeLocationFromRoute(routeId: Long, locationId: Long): Boolean {
        val db = writableDatabase
        return db.delete("route_locations", "route_id = ? AND location_id = ?", arrayOf(routeId.toString(), locationId.toString())) > 0
    }

    fun getAvailableTeams(): List<AvailableTeam> {
        val db = readableDatabase
        val list = mutableListOf<AvailableTeam>()
        val cursor = db.query("available_teams", null, null, null, null, null, "id ASC")
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val plate = cursor.getString(cursor.getColumnIndexOrThrow("vehicle_plate"))
                val capacityKg = cursor.getDouble(cursor.getColumnIndexOrThrow("capacity_kg"))
                val driverName = cursor.getString(cursor.getColumnIndexOrThrow("driver_name"))
                val members = cursor.getString(cursor.getColumnIndexOrThrow("members"))
                val isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("is_available")) == 1

                list.add(
                    AvailableTeam(
                        id = id,
                        vehiclePlate = plate,
                        capacityKg = capacityKg,
                        driverName = driverName,
                        members = members,
                        isAvailable = isAvailable
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun assignTeamToRoute(routeId: Long, team: AvailableTeam): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("vehicle_team_id", team.id)
            put("vehicle_plate", team.vehiclePlate)
            put("driver_name", team.driverName)
        }
        return db.update("routes", values, "id = ?", arrayOf(routeId.toString())) > 0
    }

    fun publishRoute(routeId: Long): Pair<Boolean, String> {
        val db = writableDatabase
        val route = getRouteById(routeId) ?: return Pair(false, "La ruta no existe")

        if (route.vehicleTeamId == null) {
            return Pair(false, "Asigne un vehículo y equipo antes de publicar")
        }

        val locations = getRouteLocations(routeId)
        if (locations.isEmpty()) {
            return Pair(false, "Asigne al menos una entrega para publicar")
        }

        val values = ContentValues().apply {
            put("status", RouteStatus.PUBLISHED.name)
        }
        val updated = db.update("routes", values, "id = ?", arrayOf(routeId.toString())) > 0
        return if (updated) Pair(true, "Ruta publicada con éxito") else Pair(false, "Error al actualizar la ruta")
    }

    fun updateDeliveryStatus(
        routeLocationId: Long,
        newStatus: LocationDeliveryStatus,
        reason: String? = null
    ): Boolean {
        val db = writableDatabase
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put("status", newStatus.name)
            if (reason != null) put("rejection_reason", reason)
            if (newStatus == LocationDeliveryStatus.COMPLETED) put("confirm_time", timeStr)
        }
        val updated = db.update("route_locations", values, "id = ?", arrayOf(routeLocationId.toString())) > 0

        if (updated) {
            val cursor = db.query("route_locations", arrayOf("route_id"), "id = ?", arrayOf(routeLocationId.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                val routeId = cursor.getLong(0)
                val route = getRouteById(routeId)
                if (route?.status == RouteStatus.PUBLISHED) {
                    val routeValues = ContentValues().apply {
                        put("status", RouteStatus.IN_PROGRESS.name)
                    }
                    db.update("routes", routeValues, "id = ?", arrayOf(routeId.toString()))
                }
            }
            cursor.close()
        }

        return updated
    }

    fun closeRoute(routeId: Long, forceWithPendings: Boolean = false): Boolean {
        val db = writableDatabase
        val locations = getRouteLocations(routeId)
        val hasPendings = locations.any { it.status == LocationDeliveryStatus.PENDING || it.status == LocationDeliveryStatus.IN_TRANSIT }

        val finalStatus = if (hasPendings || forceWithPendings) {
            RouteStatus.CLOSED_WITH_PENDINGS
        } else {
            RouteStatus.COMPLETED
        }

        val values = ContentValues().apply {
            put("status", finalStatus.name)
        }
        return db.update("routes", values, "id = ?", arrayOf(routeId.toString())) > 0
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.trim().isEmpty()
}
