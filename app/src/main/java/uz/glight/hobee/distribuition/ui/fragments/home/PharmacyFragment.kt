package uz.glight.hobee.distribuition.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import uz.glight.hobee.distribuition.network.models.ClinicModel
import com.glight.hobeedistribuition.utils.Constants.TAG
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentPharmacyBinding
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState

class PharmacyFragment : Fragment(R.layout.fragment_pharmacy) {

    private var bindingPharmacy: FragmentPharmacyBinding? = null
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPharmacyBinding.bind(view)
        bindingPharmacy = binding

        binding.listView.adapter = dataAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.datamodel.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
        viewModel.datamodel.removeObserver(dataRetriever)
        super.onPause()
    }

    private val dataRetriever = Observer<ViewState>{
        when (it) {
            is ViewState.Success<*> -> {
                Log.d(TAG, it.data.toString())
                dataAdapter.update(it.data as List<ClinicModel>)
            }
            is ViewState.Error<*> -> {
                Log.d(TAG, it.error.toString())
            }
            is ViewState.Loading -> {
                Log.d(TAG, "LOADING")
            }
        }
    }

    val listener = object : OnItemClickListener<ClinicModel> {
        override fun onClickItem(position: Int, data: ClinicModel) {
            val bundle = bundleOf("drugstore_id" to data.id, "title" to data.name)
            findNavController().navigate(R.id.to_drugstore, bundle)
        }
    }

    private val dataAdapter = object : ItemsListAdapter<ClinicModel>(listener) {
        override fun getLayoutId(position: Int, obj: ClinicModel): Int = R.layout.card_pharm

    }
}