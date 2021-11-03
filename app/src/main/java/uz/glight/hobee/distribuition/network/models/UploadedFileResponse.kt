package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName

data class UploadedFileResponse(
    @SerializedName("doctor_id")
    val doctorId: String?,
    @SerializedName("agent_id")
    val agentId: Int?,
    @SerializedName("discussion_date")
    val discussionDate: String?,
    @SerializedName("created_date")
    val createdDate: String?,
    @SerializedName("filename")
    val filename: String?,
    @SerializedName("id")
    val id: Int?
)