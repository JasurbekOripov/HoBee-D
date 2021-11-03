package uz.glight.hobee.distribuition.network.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.glight.hobee.distribuition.BuildConfig
import java.util.concurrent.TimeUnit


object RetrofitBuilder {

    private const val BASE_URL = BuildConfig.BASE_URL

    private fun getRetrofit(token: String?): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
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
            ).addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun apiService(token: String?): HobeeAPI = getRetrofit(token).create(HobeeAPI::class.java)
}