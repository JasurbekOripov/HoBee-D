package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName

data class DiscussionModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("doctor")
    val doctor: Doctor?,
    @SerializedName("agent")
    val agent: Agent?,
    @SerializedName("discussion_date")
    val discussionDate: String?,
    @SerializedName("filename")
    val filename: String,
    @SerializedName("created_at")
    val createdDate: String
) {
    data class Doctor(
        @SerializedName("id")
        val id: Int,
        @SerializedName("fullname")
        val fullname: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("about")
        val about: String,
        @SerializedName("room")
        val room: String,
        @SerializedName("break")
        val breakX: String,
        @SerializedName("specialization")
        val specialization: List<Any>
    )

    data class Agent(
        @SerializedName("user")
        val user: Int,
        @SerializedName("fullname")
        val fullname: String
    )
}