package uz.glight.hobee.distribuition.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.glight.hobeedistribuition.network.model.ErrorModel
import com.glight.hobeedistribuition.network.model.UserLogin
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.glight.hobeedistribuition.utils.PermissionUtils
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.network.repository.RemoteRepository

/**
 *
 */
class LoginActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Migrate to bindings feature
    private lateinit var fullName: EditText
    private lateinit var password: EditText
    // TODO: // create getTagName function in activity scope ibragimov.commons
    private val TAG = LoginActivity::class.java.canonicalName
    private lateinit var ma: Intent
    private lateinit var progress: ProgressBar
    private lateinit var permUtils: PermissionUtils
    private lateinit var loginBtn: Button

    private var corJob: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        permUtils = PermissionUtils(this, this)
        progress = findViewById(R.id.progressBar)
        loginBtn = findViewById(R.id.button)

        corJob = CoroutineScope(Job() + Dispatchers.IO)

        if (!permUtils.checkUserPermission()) {
            permUtils.requestPermission()
        }

        ma = Intent(this, BottomNavigationActivity::class.java)
        ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        password = findViewById(R.id.editPassword)
        fullName = findViewById(R.id.editFullName)

        ModelPreferencesManager.preferences.registerOnSharedPreferenceChangeListener(this)

        loginBtn.setOnClickListener {
            progress.visibility = View.VISIBLE
            loginBtn.isEnabled = false
            login()
        }
    }

    /**
     * Login function
     */
    private fun login() {
        if (fullName.text.isNullOrEmpty() || password.text.isNullOrEmpty()) {
            fullName.error = "Заполните все поля!"
            password.error = "Заполните все поля!"

            progress.visibility = View.GONE
            loginBtn.isEnabled = true
        } else {
            loginBtn.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(this@LoginActivity, R.color.gray)
                isEnabled = false
            }
//            loginBtn.setBackgroundColor()
            corJob?.launch {
                val response = RemoteRepository.userLogin(
                    user = UserLogin(
                        username = fullName.text.toString(),
                        password = password.text.toString()
                    )
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        onSuccess(response.body()!!)
                    }
                } else {
                    withContext(Dispatchers.Main){
                        onError(RemoteRepository.parseError(response))
                    }
                }
            }
        }
    }


    /**
     * Success callback that called when
     * RemoteRepository.userLogin request return successfull status
     */
    private fun onSuccess(data: UserModel) {
        ModelPreferencesManager.put(data, ModelPreferencesManager.PREFERENCES_FILE_NAME)
        Lingver.getInstance().setLocale(this, "ru", "UZ")
        progress.visibility = View.GONE
        loginBtn.apply {
            backgroundTintList =
                ContextCompat.getColorStateList(this@LoginActivity, R.color.blue)
            isEnabled = true
        }
    }

    /**
     * Error callback that called when
     * RemoteRepository.userLogin request return failed status
     */
    private fun onError(message: ErrorModel) {
        progress.visibility = View.GONE
        loginBtn.apply {
            backgroundTintList =
                ContextCompat.getColorStateList(this@LoginActivity, R.color.blue)
            isEnabled = true
        }
        fullName.error = message.message.toString()
        password.error = message.message.toString()
    }

    /**
     * Clear all listeners when view destroy begin
     */
    override fun onDestroy() {
        super.onDestroy()
        corJob?.cancel()
        ModelPreferencesManager.preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Request permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d(TAG, "onSharedPreferenceChanged: $key")
        if (key == ModelPreferencesManager.PREFERENCES_FILE_NAME){
            val prefData = ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)
            if (prefData?.accessToken != null){
                startActivity(ma)
                finish()
            }
        }
    }

}