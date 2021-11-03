package uz.glight.hobee.distribuition.ui.fragments.settings

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import uz.glight.hobee.distribuition.ui.activities.LoginActivity

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button: Preference? = findPreference("exit_btn")
        button?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ModelPreferencesManager.preferences.edit().clear().apply()
            val la = Intent(context, LoginActivity::class.java)
            la.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(la)
            activity?.finish()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val l = Lingver.getInstance().getLanguage()

        findPreference<ListPreference>("languages_list")?.setDefaultValue(l)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(pref: SharedPreferences?, key: String?) {
        if (key == "languages_list") {
            val lang = pref?.getString("languages_list", "ru")
            setNewLocale(lang!!)
        }
    }


    private fun setNewLocale(language: String) {
        Lingver.getInstance().setLocale(requireContext(), language, "UZ")
        restart()
    }

    private fun followSystemLocale() {
        Lingver.getInstance().setFollowSystemLocale(requireContext())
        restart()
    }

    private fun restart() {
        val i = Intent(requireContext(), BottomNavigationActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}