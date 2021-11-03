package uz.glight.hobee.distribuition.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RequestData(
    @SerializedName("items")
    val items: List<Item> = ArrayList<Item>()
) : Serializable