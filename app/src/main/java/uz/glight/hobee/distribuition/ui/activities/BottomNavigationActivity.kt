package uz.glight.hobee.distribuition.ui.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.services.NetworkChangeListener
import uz.glight.hobee.distribuition.utils.LocationReceiver
import uz.glight.hobee.distribuition.utils.NetworkHelper
import java.lang.Exception

class BottomNavigationActivity : AppCompatActivity() {
    val br: BroadcastReceiver = NetworkChangeListener()
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    // App Bar configuration

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        val userData =
            ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        if (NetworkHelper(applicationContext).isNetworkConnected()) {
            RemoteRepository.setService(userData?.accessToken!!)
        } else {
            Snackbar.make(toolbar, "No internet connection", Snackbar.LENGTH_SHORT).show()
        }

        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        /**
         * Hide bottom navigation view
         * if current destination under the top
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (NetworkHelper(applicationContext).isNetworkConnected()) {
                lifecycleScope.launch {
                    try {
                        var res = RemoteRepository.getDiscounts()
                        if (res.code() > 400) {
                            Snackbar.make(
                                navView,
                                "You access is failed , please re-enter again",
                                Snackbar.LENGTH_LONG
                            ).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                val la = Intent(applicationContext, LoginActivity::class.java)
                                la.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(la)
                                finish()
                            }, 1500)

                        }
                    } catch (e: Exception) {
                        Snackbar.make(navView, "No internet connection", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                }
                if (destination.id == R.id.navigation_home || destination.id == R.id.navigation_notifications || destination.id == R.id.navigation_dashboard || destination.id == R.id.settingsFragment) {
                    navView.visibility = View.VISIBLE
                } else {
                    navView.visibility = View.GONE
                }
            } else {
                Snackbar.make(navView, "No internet connection", Snackbar.LENGTH_SHORT)
                    .show()
            }

        }
    }

    /**
     * Back button press navigation support
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        //location service
        getLocationPermission()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (locationPermissionGranted) {
            var service: BroadcastReceiver = LocationReceiver()
            registerReceiver(service, filter)
        }
        registerReceiver(br, filter)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
    }
}