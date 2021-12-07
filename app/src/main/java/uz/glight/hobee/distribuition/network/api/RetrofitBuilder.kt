package uz.glight.hobee.distribuition.network.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uz.glight.hobee.distribuition.BuildConfig
import java.lang.Exception
import java.util.concurrent.TimeUnit


object RetrofitBuilder {

    private const val BASE_URL = "http://distributor.hobee.uz/v1/api/"
//    private const val BASE_URL = "http://192.168.0.179/v1/api/"    //local base url

    private fun getRetrofit(token: String?): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(interceptor2(token ?: ""))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun interceptor2(token: String) =
        Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = if (token != null) {

                original.newBuilder()
                    .addHeader("From-Mobile", BuildConfig.APPLICATION_ID)
                    .addHeader("Authorization", "Bearer $token")
                    .method(original.method, original.body)
                    .build()
            } else {
                original.newBuilder()
                    .addHeader("From-Mobile", BuildConfig.APPLICATION_ID)
                    .method(original.method, original.body)
                    .build()
            }
            chain.proceed(request)
        }

    fun apiService(token: String?): HobeeAPI = getRetrofit(token).create(HobeeAPI::class.java)
}