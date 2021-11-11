package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pharmacy")
    val pharmacy: Pharmacy?=Pharmacy(),
    @SerializedName("agent")
    val agent: Agent,
    @SerializedName("discount")
    val discount: String?,
    @SerializedName("total_payment_sum")
    val totalPaymentSum: String?,
    @SerializedName("remained_payment")
    val remainedPayment: String?,
    @SerializedName("payment_type")
    val paymentType: String,
    // Finished = 1
    // Canceled = 2
    // New = 0
    @SerializedName("status")
    val status: Int,
    // Finished = 1
    // Not finished = 0
    @SerializedName("payment_status")
    val paymentStatus: Int,
    // Finished = 1
    // Not finished = 0
    @SerializedName("shipment_status")
    val shipmentStatus: Int,
    @SerializedName("items_rel")
    val itemsRel: List<ItemsRel>,
    @SerializedName("payment_info")
    val paymentInfo: List<PaymentInfo>,
    @SerializedName("created_at")
    val createdAt: String
) : Serializable {
    data class Pharmacy(
        @SerializedName("name")
        val name: String?="",
        @SerializedName("address")
        val address: String?="",
        @SerializedName("reception_phone")
        val receptionPhone: String?=""
    ) : Serializable

    data class Agent(
        @SerializedName("user")
        val user: Int,
        @SerializedName("fullname")
        val fullname: String
    ) : Serializable

    data class ItemsRel(
        @SerializedName("warehouse_id")
        val warehouseId: Any?,
        @SerializedName("catalogue_medicine_id")
        val catalogueMedicineId: Int,
        @SerializedName("price")
        val price: String,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("total_price")
        val totalPrice: String
    ) : Serializable

    data class PaymentInfo(
        @SerializedName("id")
        val id: Int,
        @SerializedName("request_agent_id")
        val requestAgentId: Int,
        @SerializedName("payment_type")
        val paymentType: Any?,
        @SerializedName("sum")
        val sum: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("created_by")
        val createdBy: Int,
        @SerializedName("deleted_at")
        val deletedAt: Any?,
        @SerializedName("deleted_status")
        val deletedStatus: Any?
    ) : Serializable
}