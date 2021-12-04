package uz.glight.hobee.distribuition.network.api

import com.glight.hobeedistribuition.network.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Url

import okhttp3.ResponseBody
import retrofit2.Call

import retrofit2.http.GET
import uz.glight.hobee.distribuition.network.models.*


interface HobeeAPI {

    /////////////////////////////////////////////////////////////////////////////////////////////

    //No Pagination
    @POST("user/login-agent")
    suspend fun loginUser(@Body usr: UserLogin): Response<UserModel>


    @POST("request-mobile/create")
    suspend fun createOrder(
        @Body clinicName: CreateOrderModel
    ): Response<CreatedResponseModel>


    @Multipart
    @POST("doctors/discussion")
    suspend fun sendRecord(
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<UploadedFileResponse>

    @POST("agents-live-location")
    fun sendLocation(
        @Body user: UserLocation,
    ): Call<LocationResponse>

    /////////////////////////////////////////////////////////////////////////////////////////////

    //Pagination
    @GET
    suspend fun getRequests(@Url url: String): Response<List<Item>>              //ready

    @GET("warehouse")
    suspend fun getWarehouse(
        // @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("per-page") perPage: Int = 25,
        @Query("search") query: String
    ): Response<List<DrugModel>>                                                 //ready

    @GET("discussions")
    suspend fun getDiscussionList(
        @Query("page") page: Int,
        @Query("per-page") perPage: Int = 25,
    ): Response<List<DiscussionModel>>                                           //ready

    @GET("request-agent")
    suspend fun getApplicationsList(
        @Query("page") page: Int,
        @Query("per-page") perPage: Int = 30,
    ): Response<List<OrderModel>>                                                //ready


    @GET("prepayment-discount")
    suspend fun getDiscount(): Response<List<DiscountModel>>                        //ready


    @GET("doctors")
    suspend fun getDoctors(
        // @Header("Authorization") auth: String,
        @Query("clinic") clinic_id: String,
        @Query("page") page: Int,
        @Query("fullname") doctorName: String?
    ): Response<List<DoctorModel>>                                                  //ready


    @GET("clinic")
    suspend fun getClinics(
        // @Header("Authorization") auth: String,
        @Query("name") query: String?,
        @Query("page") page: Int
    ): Response<List<ClinicModel>>                                                  //ready


    @GET("pharmacy")
    suspend fun getPharmacy(
        @Query("name") query: String?,
        @Query("per-page") limit: Int? = 30,
        @Query("page") page: Int?
    ): Response<List<ClinicModel>>                                                  //ready


}

