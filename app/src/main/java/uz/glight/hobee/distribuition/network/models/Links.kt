package uz.glight.hobee.distribuition.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Links(
    @SerializedName("first")
    val first: First = First(""),
    @SerializedName("last")
    val last: Last = Last(""),
    @SerializedName("self")
    val self: Self = Self("")
):Serializable