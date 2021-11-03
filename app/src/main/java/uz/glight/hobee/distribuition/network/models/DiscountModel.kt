package uz.glight.hobee.distribuition.network.models


import com.google.gson.annotations.SerializedName

data class DiscountModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("discount")
    val discount: String,
    @SerializedName("prepayment")
    val prepayment: String,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: Int,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("deleted_status")
    val deletedStatus: Int
)