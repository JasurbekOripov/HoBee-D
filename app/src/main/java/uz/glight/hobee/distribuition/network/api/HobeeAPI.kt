package uz.glight.hobee.distribuition.network.api

import com.glight.hobeedistribuition.network.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.network.models.DiscountModel
import uz.glight.hobee.distribuition.network.models.RequestData
import retrofit2.http.Url

import okhttp3.ResponseBody
import retrofit2.Call

import retrofit2.http.GET
import uz.glight.hobee.distribuition.network.models.Item


interface HobeeAPI {
    @Multipart
    @POST("doctors/discussion")
    suspend fun sendRecord(
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<UploadedFileResponse>

    @POST("request-agent/create")
    suspend fun createOrder(
        @Body clinicName: CreateOrderModel
    ): Response<CreatedResponseModel>

    @GET("prepayment-discount")
    suspend fun getDiscount(): Response<List<DiscountModel>>

    @GET("pharmacy")
    suspend fun getPharmacy(
        @Query("name") query: String?
    ): Response<List<ClinicModel>>

    @GET("clinic")
    suspend fun getClinics(
        // @Header("Authorization") auth: String,
        @Query("name") query: String?,
        @Query("page") page: Int = 1
    ): Response<List<ClinicModel>>

    @GET("doctors")
    suspend fun getDoctors(
        // @Header("Authorization") auth: String,
        @Query("clinic") clinic_id: String,
        @Query("fullname") doctorName: String?

    ): Response<List<DoctorModel>>

    @GET("warehouse")
    suspend fun getWarehouse(
        // @Header("Authorization") auth: String,
        @Query("page") page: Int = 1,
        @Query("per-page") perPage: Int = 25,
        @Query("search") query: String = ""
    ): Response<List<DrugModel>>

    @GET("discussions")
    suspend fun getDiscussionList(
        @Query("page") page: Int = 1,
        @Query("per-page") perPage: Int = 25,
    ): Response<List<DiscussionModel>>

    @GET("request-agent")
    suspend fun getApplicationsList(
        @Query("page") page: Int = 1,
        @Query("per-page") perPage: Int = 30,
    ): Response<List<OrderModel>>

    @GET
    fun getRequests(@Url url:String ): Call<List<Item>>

    @POST("user/login-agent")
    suspend fun loginUser(@Body usr: UserLogin): Response<UserModel>
}

