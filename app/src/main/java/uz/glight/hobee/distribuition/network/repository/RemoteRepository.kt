package uz.glight.hobee.distribuition.network.repository

import com.glight.hobeedistribuition.network.model.*
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*
import retrofit2.http.Query
import uz.glight.hobee.distribuition.network.api.RetrofitBuilder
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.network.models.DiscountModel
import uz.glight.hobee.distribuition.network.models.Item
import uz.glight.hobee.distribuition.network.models.RequestData


object RemoteRepository {

    val TOKEN =
        ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)?.accessToken
    private var service = RetrofitBuilder.apiService(null)

    fun setService(token: String) {
        service = RetrofitBuilder.apiService(token)
    }

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

    fun toRequestBody(value: String): RequestBody {
        val body: RequestBody = value.toRequestBody("*/*".toMediaTypeOrNull());
        return body;
    }


    suspend fun userLogin(user: UserLogin): Response<UserModel> {
        val response = service.loginUser(user)
        return response
    }

    suspend fun getClinic(query: String,page:Int): Response<List<ClinicModel>> {
        return service.getClinics(query,page)
    }

    suspend fun getPharmacy(query: String,page:Int): Response<List<ClinicModel>> {
        return service.getPharmacy(query,page)
    }

//    suspend fun getClinic(): Response<List<ClinicModel>> {
//        return service.getClinics(null)
//    }

//    suspend fun getPharmacy(): Response<List<ClinicModel>> {
//        return service.getPharmacy(null)
//    }

    suspend fun getDiscounts(/*page:Int*/): Response<List<DiscountModel>> {
        return service.getDiscount()
    }

    suspend fun getWarehouse(page:Int,str:String): Response<List<DrugModel>> {
        return service.getWarehouse(page = page,query = str)
    }

//    suspend fun getWarehouse(query: String): Response<List<DrugModel>> {
//        return service.getWarehouse(query = query)
//    }

    suspend fun getDoctors(clinic_id: String, page:Int,name: String = ""): Response<List<DoctorModel>> {
        return service.getDoctors(clinic_id, page,name)
    }

    suspend fun getDiscussionList(page:Int): Response<List<DiscussionModel>> {
        return service.getDiscussionList(page)
    }

    suspend fun getApplicationsList(page: Int): Response<List<OrderModel>> {
        return service.getApplicationsList(page)
    }

    suspend fun createApplication(data: CreateOrderModel): Response<CreatedResponseModel> {
        return service.createOrder(data)
    }

    suspend fun getRespons(url: String): Response<List<Item>> {
        return service.getRequests(url)
    }


    suspend fun sendRecord(
        data: Map<String, RequestBody>
    ): Response<UploadedFileResponse> {
        return service.sendRecord(data)
    }
}
