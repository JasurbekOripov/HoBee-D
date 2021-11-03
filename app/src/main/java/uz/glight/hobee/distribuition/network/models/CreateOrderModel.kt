package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateOrderModel(
    @SerializedName("drugstore_id")
    val id: Int?,
    @SerializedName("agent_id")
    val agentId: Int?,
    @SerializedName("discount")
    var sale: String?,
    @SerializedName("prepayment")
    var prepayment: String?,
    @SerializedName("total_price")
    val generalPrice: String?,
    @SerializedName("payment_type")
    var paymentType: String?,
    @SerializedName("list_of_drugs")
    val listOfDrugs: List<DrugsListItem?>?
): Serializable {
    data class DrugsListItem(
        @SerializedName("name")
        val name: String?,
        @SerializedName("warehouse_id")
        val warehouseId: Int?,
        @SerializedName("medicament_id")
        val id: Int?,
        @SerializedName("price_for_one")
        val priceForOne: String?,
        @SerializedName("amount")
        var amount: Int?,
        @SerializedName("total_price")
        var allPrice: Double
    ): Serializable
}