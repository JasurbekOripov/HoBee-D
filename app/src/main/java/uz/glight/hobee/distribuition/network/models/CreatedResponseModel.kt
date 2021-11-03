package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName

data class CreatedResponseModel(
    @SerializedName("pharmacy_id")
    val pharmacyId: Int?,
    @SerializedName("agent_id")
    val agentId: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("discount")
    val discount: String?,
    @SerializedName("total_payment_sum")
    val totalPaymentSum: String?,
    @SerializedName("remained_payment")
    val remainedPayment: String?,
    @SerializedName("payment_type")
    val paymentType: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("payment_status")
    val paymentStatus: Int?,
    @SerializedName("shipment_status")
    val shipmentStatus: Int?,
    @SerializedName("id")
    val id: Int?
)