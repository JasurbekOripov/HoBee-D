package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DoctorModel(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("fullname")
    val name: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("specialization")
    val specialization: List<Specialization>? = emptyList(),
    @SerializedName("about")
    val about: String?,
    @SerializedName("room")
    val room: String?,
    @SerializedName("break")
    val coffeeBreak: String?
): Serializable {
    data class Specialization(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    ) : Serializable
}