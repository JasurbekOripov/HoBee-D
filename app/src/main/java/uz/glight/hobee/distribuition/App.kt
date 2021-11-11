package uz.glight.hobee.distribuition

import android.app.Application
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ModelPreferencesManager.with(this)
        MapKitFactory.setApiKey("6f955daa-1531-413f-bc43-565e351466f0")
        val store = PreferenceLocaleStore(this, Locale("ru"))
        Lingver.init(this, store)
    }
}