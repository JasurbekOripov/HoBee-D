package uz.glight.hobee.distribuition.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ClinicAdapter
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentClinicsBinding
import uz.glight.hobee.distribuition.utils.NetworkHelper

class ClinicsFragment : Fragment(R.layout.fragment_clinics) {
    private lateinit var adapter: ClinicAdapter
    private var bindingPharmacy: FragmentClinicsBinding? = null
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentClinicsBinding.bind(view)
        bindingPharmacy = binding

        adapter = ClinicAdapter(object : ClinicAdapter.setOnClick {
            override fun itemClick(data: ClinicModel, position: Int) {
                findNavController().navigate(
                    R.id.to_hospital_fragment,
                    bundleOf("clinic" to data, "title" to data.name)
                )
            }
        })
        binding.listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            view?.let { Snackbar.make(it, "No internet connection", Snackbar.LENGTH_SHORT).show() }
        }
    }

    private fun loadData() {
        viewModel.datamodelClinic.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                it.let {data->
                    if (data!=null)
                    adapter.submitData(data)
                }
            }
        })
    }

}