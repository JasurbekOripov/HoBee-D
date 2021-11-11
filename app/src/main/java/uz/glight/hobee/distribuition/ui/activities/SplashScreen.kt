package uz.glight.hobee.distribuition.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.android.material.snackbar.Snackbar
import uz.glight.hobee.distribuition.BuildConfig
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.utils.AnimationHelper
import uz.glight.hobee.distribuition.utils.NetworkHelper

/**
 *
 * Splash Screen that show up when ap is activated then navigate user to destination
 *
 * destination_1 = if user sign in, user token saved into device
 * destination_2 = user not authorized, he must sign in for use the application
 *
 */
class SplashScreen : AppCompatActivity() {
    private lateinit var tx: TextView
    private lateinit var welcomeTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)

        tx = findViewById(R.id.textView6)
        welcomeTV = findViewById(R.id.textView3)
        tx.text = "v${BuildConfig.VERSION_NAME}"


        /**
         * Animation for "Welcome" text
         */
        AnimationHelper.showAnimation(welcomeTV, null)

        /**
         * destination_2
         */
        val la = Intent(this, LoginActivity::class.java)
        la.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        /**
         * destination_1
         */
        val ma = Intent(this, BottomNavigationActivity::class.java)
        ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)


        /**
         * get user that saved into device, if user not saved return null
         */
        if (NetworkHelper(applicationContext).isNetworkConnected()) {

            val user =
                ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)

            object : CountDownTimer(1000, 1000) {
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {

                    if (user?.accessToken == null) {
                        startActivity(la)
                        finish()
                    } else {
                        startActivity(ma)
                        finish()
                    }
                }
            }.start()
        } else {
            Snackbar.make(tx, "No internet connection", Snackbar.LENGTH_SHORT).show()
        }

    }
}