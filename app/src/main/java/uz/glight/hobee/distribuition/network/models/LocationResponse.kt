package uz.glight.hobee.distribuition.network.models

data class LocationResponse(
    val agent_id: Int,
    val bearing: Float,
    val bearing_accuracy: Float,
    val company_id: Int,
    val datetime: String,
    val id: Int,
    val latitude: Double,
    val longitude: Double
)