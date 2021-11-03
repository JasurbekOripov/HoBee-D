package uz.glight.hobee.ibrogimov.commons

import com.glight.hobeedistribuition.network.model.ErrorModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

fun parseError(response: Response<*>): ErrorModel {
    val gson = Gson()
    val type = object : TypeToken<ErrorModel>() {}.type
    val defError = ErrorModel(
        "Unhandled",
        "Sorry we take unhandled error",
        1700,
        5000,
        "unhandled_error"
    )

    val errorResponse: ErrorModel?
    try {
        errorResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
    } catch (e: Exception) {
        return defError
    }

    return errorResponse ?: defError

}