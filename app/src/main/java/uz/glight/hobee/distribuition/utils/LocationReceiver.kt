package uz.glight.hobee.distribuition.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.glight.hobee.distribuition.network.models.LocationResponse
import uz.glight.hobee.distribuition.network.models.UserLocation
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.text.SimpleDateFormat
import java.util.*

class LocationReceiver : BroadcastReceiver() {
    var lastTime = 0L
    lateinit var networkHelper: NetworkHelper
    val userData =
        ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)

    override fun onReceive(context: Context, intent: Intent) {
        networkHelper = NetworkHelper(context)
        getDeviceLocation(context)
    }

    @SuppressLint("SimpleDateFormat", "VisibleForTests")
    fun getDeviceLocation(context: Context) {
        var currentTime = System.currentTimeMillis()
        try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("TAGLoc06", "getDeviceLocation: not equal worked")
                    Toast.makeText(
                        context,
                        "Permissions denied please accept them ! ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                val locationResult = FusedLocationProviderClient(context).lastLocation
                locationResult.addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result != null) {
                            if (networkHelper.isNetworkConnected()) {
                                var bearnig = 0F
                                if (it.result.hasBearing()) {
                                    bearnig = it.result?.bearing ?: 0F
                                }
                                if (currentTime - lastTime > 1000) {
                                    lastTime = currentTime
                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val currentDateandTime: String = sdf.format(Date())
                                    Log.d("TAGLoc06", "getDeviceLocationReal:lat ${it.result?.latitude} " +
                                            " long  ${it.result?.longitude}")
                                    var res = RemoteRepository.sendLocation(
                                        UserLocation(
                                            bearing = bearnig,
                                            datetime = currentDateandTime,
                                            latitude = it.result?.latitude ?: 0.0,
                                            longitude = it.result?.longitude ?: 0.0,
                                            bearing_accuracy = 0F,
                                            agent_id = userData?.id ?: 1
                                        )
                                    )
                                    res.enqueue(object : Callback<LocationResponse> {
                                        override fun onResponse(
                                            call: Call<LocationResponse>,
                                            response: Response<LocationResponse>
                                        ) {
                                            if (!response.isSuccessful || response.code() > 400) {
                                                Toast.makeText(
                                                    context,
                                                    response.message().toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }

                                        override fun onFailure(
                                            call: Call<LocationResponse>,
                                            t: Throwable
                                        ) {

                                            Toast.makeText(
                                                context,
                                                t.message.toString(),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                                    Log.d("TAGLoc06", "location catch:${it.result} ")
                                }
                            }
                        }
                    }
                    getDeviceLocation(context)
                }
            locationResult.addOnSuccessListener {
                Log.d("TAG", "getDeviceLocationSuccess:${it} ")
            }
        } catch (e: Exception) {
            Snackbar.make(
                View(context),
                "Error with GPS please check internet adn GPS",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}