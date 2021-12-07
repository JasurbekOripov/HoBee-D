package uz.glight.hobee.distribuition.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.glight.hobee.distribuition.network.models.LocationResponse
import uz.glight.hobee.distribuition.network.models.UserLocation
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.text.SimpleDateFormat
import java.util.*

class MyLocationHelper:BroadcastReceiver() {
    var lastTime = 0L
    lateinit var networkHelper: NetworkHelper
    val userData =
        ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)
    override fun onReceive(context: Context, intent: Intent?) {
        networkHelper = NetworkHelper(context)
        val locationTracker = LocationTracker( 1000,
            10f, shouldUseGPS = true, shouldUseNetwork = true, shouldUsePassive = true)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationTracker.startListening(context)
        }
        locationTracker.addListener(object: LocationTracker.Listener {

            @SuppressLint("SimpleDateFormat")
            override fun onLocationFound(location: Location) {
                Log.d("TAG", "onLocationFound:${location} ")
                var currentTime = System.currentTimeMillis()
                try {
                    if (networkHelper.isNetworkConnected()) {
                        var bearnig = 0F
                        if (location.hasBearing()) {
                            bearnig = location.bearing
                        }
                        if (currentTime - lastTime > 1000) {
                            lastTime = currentTime
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val currentDateandTime: String = sdf.format(Date())
                                        Log.d("TAGLoc06", "getDeviceLocationReal:lat ${location.latitude} " +
                                                " long  ${location.longitude}")
                            var res = RemoteRepository.sendLocation(
                                UserLocation(
                                    bearing = bearnig,
                                    datetime = currentDateandTime,
                                    latitude = location.latitude ?: 0.0,
                                    longitude = location.longitude ?: 0.0,
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

                        }
                        onReceive(context,intent)
                    }
                } catch (e: Exception) {
//                    Toast.makeText(context, "Error with GPS please check internet adn GPS" , Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, " ${e.message}" , Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "onError: ${e.message}")
                }
            }

            override fun onProviderError(providerError: ProviderError) {
                Log.d("TAG", "onLocationFoundE:${providerError.message} ")

            }
        });

    }

}