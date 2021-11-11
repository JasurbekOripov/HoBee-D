package uz.glight.hobee.distribuition.network.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClinicModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("reception_phone")
    val receptionPhone: String?,
    var latitude: Double,
    var longitude: Double,
    var status: String = "",
    var statusText: String = "",
    var remainingPayment: Long
) : Serializable