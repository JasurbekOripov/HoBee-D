package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("fullname")
    val fullname: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("positions")
    val positions: List<Position?>?
) {
    data class Position(
        @SerializedName("position_id")
        val positionId: Int?,
        @SerializedName("position")
        val position: String?
    )
}