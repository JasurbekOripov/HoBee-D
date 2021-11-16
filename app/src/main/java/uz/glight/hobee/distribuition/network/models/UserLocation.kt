package uz.glight.hobee.distribuition.network.models

data class UserLocation(
    val agent_id: Int,
    val bearing: Float,
    val bearing_accuracy: Float,
    val datetime: String,
    val latitude: Double,
    val longitude: Double
)