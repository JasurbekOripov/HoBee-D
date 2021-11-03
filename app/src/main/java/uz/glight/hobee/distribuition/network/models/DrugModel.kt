package com.glight.hobeedistribuition.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DrugModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("catalogueMedicine")
    val catalogueMedicine: CatalogueMedicine,
    @SerializedName("quantity_package")
    val quantityPackage: Int,
    @SerializedName("reserved")
    val reserved: Int,
    @SerializedName("purchase_id")
    val purchaseId: Int,
    @SerializedName("purchase_item_id")
    val purchaseItemId: Int,
    @SerializedName("purchase_price")
    val purchasePrice: String,
    @SerializedName("sell_price")
    val sellPrice: String,
    @SerializedName("cell")
    val cell: String,
    @SerializedName("expire_date")
    val expireDate: String,
    @SerializedName("isChecked")
    var isChecked: Boolean = false
) : Serializable {
    fun setCheckedCard(checked: Boolean){
        isChecked = checked
    }
    data class CatalogueMedicine(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("dosage_info")
        val dosageInfo: String,
        @SerializedName("dosageForm_rel")
        val dosageFormRel: DosageFormRel,
        @SerializedName("components")
        val components: Any?,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("packaging")
        val packaging: String,
        @SerializedName("dispensingForm_rel")
        val dispensingFormRel: DispensingFormRel,
        @SerializedName("divisible_cons")
        val divisibleCons: String,
        @SerializedName("on_prescription_cons")
        val onPrescriptionCons: String,
        @SerializedName("country_rel")
        val countryRel: CountryRel,
        @SerializedName("manufacturer")
        val manufacturer: String,
        @SerializedName("group_rel")
        val groupRel: Any?,

    ) : Serializable {

        data class DosageFormRel(
            @SerializedName("name")
            val name: String
        )

        data class DispensingFormRel(
            @SerializedName("name")
            val name: String
        )

        data class CountryRel(
            @SerializedName("name")
            val name: String
        )
    }
}