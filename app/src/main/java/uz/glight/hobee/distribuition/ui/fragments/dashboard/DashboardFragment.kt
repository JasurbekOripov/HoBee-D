package uz.glight.hobee.distribuition.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentDashboardBinding
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import java.lang.Exception

class DashboardFragment : Fragment() {
    lateinit var mapView: MapView
    var defaultPoint = Point(41.311081, 69.240562)
    lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        MapKitFactory.initialize(requireContext())
        val root = binding.root
        mapView = binding.mapView
        mapView.map.move(
            CameraPosition(defaultPoint, 14f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0F), null
        )
        loadData()
        return root
    }

    private fun loadData() {
        var liveData = MutableLiveData<List<ClinicModel>>()
        var page = 1
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                myLoop@ while (true) {
                    var res = RemoteRepository.getPharmacy("", 30, page)
                    if (res.isSuccessful) {
                        liveData.postValue(res.body())
                        page++
                        if (res.body()?.size ?: 1 < 30) {
                            break@myLoop
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Snackbar.make(requireView(), "Error", Snackbar.LENGTH_SHORT).show()
        }
        liveData.observe(viewLifecycleOwner, {
            for (i in liveData.value ?: emptyList()) {
                setText(i)
            }
        })

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setText(s: ClinicModel) {
        var pharmacyNametv = TextView(requireContext())
        var params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        try {
            val drawble =
                (activity as BottomNavigationActivity).getDrawable(R.drawable.price_tv_back)
            pharmacyNametv.background = drawble
            pharmacyNametv.layoutParams = params
            pharmacyNametv.text = s.name
            pharmacyNametv.setPadding(15, 7, 15, 7)
            pharmacyNametv.setTextColor(Color.WHITE)
            pharmacyNametv.setTypeface(null, Typeface.BOLD);
            val viewProvider = ViewProvider(pharmacyNametv)
            val viewPlacemark: PlacemarkMapObject =
                mapView.map.mapObjects.addPlacemark(Point(s.latitude, s.longitude), viewProvider)
            viewProvider.snapshot()
            viewPlacemark.setView(viewProvider)
        } catch (e: Exception) {
            Snackbar.make(requireView(), "${s.name} Error", Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }
}