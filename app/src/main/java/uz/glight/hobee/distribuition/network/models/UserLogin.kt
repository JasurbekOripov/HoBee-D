package com.glight.hobeedistribuition.network.model

import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String)
