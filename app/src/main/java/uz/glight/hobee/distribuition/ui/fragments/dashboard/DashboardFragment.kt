package uz.glight.hobee.distribuition.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import uz.glight.hobee.distribuition.databinding.LocationLayerBinding
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.internetError
import uz.glight.hobee.distribuition.utils.simpleError
import java.lang.Exception

class DashboardFragment : Fragment() {
    lateinit var mapView: MapView
    var defaultPoint = Point(41.311081, 69.240562)
    lateinit var binding: FragmentDashboardBinding
lateinit var container2:ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        MapKitFactory.initialize(requireContext())
        if (container != null) {
            container2=container
        }
        val root = binding.root
        mapView = binding.mapView
        mapView.map.move(
            CameraPosition(defaultPoint, 11f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0F), null
        )
        return root
    }

    override fun onResume() {
        super.onResume()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            requireView().internetError()
        }
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
           requireView().simpleError("Ошибка")
        }
        liveData.observe(viewLifecycleOwner, {
            for (i in liveData.value ?: emptyList()) {
                setText(i)
            }
        })

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setText(s: ClinicModel) {
        var pharmacyNametv = LocationLayerBinding.inflate(layoutInflater,container2,false)
        try {
            pharmacyNametv.tvMarker.text = s.name
            val viewProvider = ViewProvider(pharmacyNametv.laynerRoot)
            val viewPlacemark: PlacemarkMapObject =
                mapView.map.mapObjects.addPlacemark(Point(s.latitude, s.longitude), viewProvider)
            viewProvider.snapshot()
            viewPlacemark.setView(viewProvider)
        } catch (e: Exception) {
            requireView().simpleError("${s.name} Ошибка")
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