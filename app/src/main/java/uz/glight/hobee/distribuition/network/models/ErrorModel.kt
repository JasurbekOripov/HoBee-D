package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("name")
    val name: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("type")
    val type: String?
)