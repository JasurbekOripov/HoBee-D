package uz.glight.hobee.distribuition.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Item(
    @SerializedName("catalogueMedicineName")
    val catalogueMedicineName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("catalogueMedicineDosageForm")
    val catalogueMedicineDosageForm: String,
    @SerializedName("catalogueMedicineCountry")
    val catalogueMedicineCountry: String,
    @SerializedName("catalogueMedicineDispensingForm")
    val catalogueMedicineDispensingForm: String,
    @SerializedName("shipped_quantity")
    val shipped_quantity: Int,
    @SerializedName("total_price")
    val total_price: String,
    @SerializedName("warehouse_id")
    val warehouse_id: Int
) : Serializable