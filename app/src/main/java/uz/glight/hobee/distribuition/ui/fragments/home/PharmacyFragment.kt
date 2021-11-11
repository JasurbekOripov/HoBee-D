package uz.glight.hobee.distribuition.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import uz.glight.hobee.distribuition.network.models.ClinicModel
import com.glight.hobeedistribuition.utils.Constants.TAG
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.adapters.PharmacyAdapter
import uz.glight.hobee.distribuition.databinding.FragmentPharmacyBinding
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState
import java.lang.Exception

class PharmacyFragment : Fragment(R.layout.fragment_pharmacy) {
    lateinit var adaptep: PharmacyAdapter
    lateinit var networkHelper: NetworkHelper
    private var bindingPharmacy: FragmentPharmacyBinding? = null
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPharmacyBinding.bind(view)
        networkHelper = NetworkHelper(requireContext())
        bindingPharmacy = binding
        adaptep = PharmacyAdapter(object : PharmacyAdapter.setOnClick {
            override fun itemClick(data: ClinicModel, position: Int) {
                try {
                    val bundle = bundleOf("drugstore_id" to data.id, "title" to data.name)
                    findNavController().navigate(R.id.drugStoreFragment, bundle)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }

        })
        binding.listView.adapter = adaptep
    }

    override fun onResume() {
        super.onResume()
        if (networkHelper.isNetworkConnected()) {
            loadData()
        } else {
            view?.let { Snackbar.make(it, "No internet connection", Snackbar.LENGTH_SHORT).show() }
        }
    }

    private fun loadData() {
        var p = viewModel.datamodel.value
        lifecycleScope.launch {
            if (p != null) {
                adaptep.submitData(p)
            }
        }
        viewModel.datamodel.observe(this, {
            lifecycleScope.launch {
                it.let { data ->
                    if (data != null)
                        adaptep.submitData(data)
                }
            }
        })
    }

}