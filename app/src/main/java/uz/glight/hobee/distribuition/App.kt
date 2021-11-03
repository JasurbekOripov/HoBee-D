package uz.glight.hobee.distribuition

import android.app.Application
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ModelPreferencesManager.with(this)

        val store = PreferenceLocaleStore(this, Locale("ru"))
        Lingver.init(this, store)
    }
}