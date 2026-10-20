package com.example.rutana.data.planning

enum class RouteStatus(val displayName: String) {
    DRAFT("Borrador"),
    PUBLISHED("Publicada"),
    IN_PROGRESS("En progreso"),
    COMPLETED("Completada"),
    CLOSED_WITH_PENDINGS("Cerrada c/ pendientes")
}

enum class LocationDeliveryStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_TRANSIT("En tránsito"),
    COMPLETED("Completado"),
    REJECTED("Rechazado")
}

data class Route(
    val id: Long = 0,
    val code: String,
    val name: String,
    val type: String,
    val colorTag: String,
    val date: String,
    val status: RouteStatus = RouteStatus.DRAFT,
    val vehicleTeamId: Long? = null,
    val vehiclePlate: String? = null,
    val driverName: String? = null,
    val locationCount: Int = 0,
    val completedCount: Int = 0
)

data class RouteLocationItem(
    val id: Long = 0,
    val routeId: Long,
    val locationId: Long,
    val clientName: String,
    val locationName: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val status: LocationDeliveryStatus = LocationDeliveryStatus.PENDING,
    val rejectionReason: String? = null,
    val confirmTime: String? = null
)

data class AvailableLocation(
    val id: Long,
    val clientName: String,
    val locationName: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    var isReservedToday: Boolean = false
)

data class AvailableTeam(
    val id: Long,
    val vehiclePlate: String,
    val capacityKg: Double,
    val driverName: String,
    val members: String,
    val isAvailable: Boolean = true
)
