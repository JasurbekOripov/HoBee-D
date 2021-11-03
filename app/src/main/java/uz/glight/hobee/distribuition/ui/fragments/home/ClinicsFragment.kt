package uz.glight.hobee.distribuition.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentClinicsBinding
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState

class ClinicsFragment : Fragment(R.layout.fragment_clinics) {
    private lateinit var adapter: ItemsListAdapter<ClinicModel>
    private var bindingPharmacy: FragmentClinicsBinding? = null
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentClinicsBinding.bind(view)
        bindingPharmacy = binding

        adapter = dataAdapter
        binding.listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.datamodelClinic.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
        viewModel.datamodelClinic.removeObserver(dataRetriever)
        super.onPause()
    }

    private val dataRetriever = Observer<ViewState> {
        when (it) {
            is ViewState.Success<*> -> {
//                Log.d(getFragmentTag(), it.data.toString())
                dataAdapter.update(it.data as List<ClinicModel>)
            }
            is ViewState.Error<*> -> {
//                Log.d(getFragmentTag(), it.error.toString())
            }
            is ViewState.Loading -> {
//                Log.d(getFragmentTag(), "LOADING")
            }
        }
    }

    val listener = object : OnItemClickListener<ClinicModel>{
        override fun onClickItem(position: Int, data: ClinicModel) {
            findNavController().navigate(R.id.to_hospital_fragment, bundleOf("clinic" to data, "title" to data.name))
        }
    }

    private val dataAdapter = object : ItemsListAdapter<ClinicModel>(listener) {
        override fun getLayoutId(position: Int, obj: ClinicModel): Int = R.layout.card_pharm
    }
}