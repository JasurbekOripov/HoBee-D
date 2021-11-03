package uz.glight.hobee.distribuition.ui.fragments.hospital

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.DoctorModel
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentHospitalBinding
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

class HospitalFragment : Fragment(R.layout.fragment_hospital) {

    private val viewModel: HospitalViewModel by viewModels()
    private var hospitalBinding: FragmentHospitalBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHospitalBinding.bind(view)
        hospitalBinding = binding
        binding.listView.adapter = dataAdapter
        binding.extendedFab.setOnClickListener {
            findNavController().navigate(R.id.to_discussion_list)
        }
        val clinic = requireArguments().getSerializable("clinic") as ClinicModel
        Log.d(getFragmentTag(), "onViewCreated: $clinic")
        viewModel.getDoctors(clinic.id.toString())
    }

    private val dataRetriever = Observer<ViewState>{
        when (it) {
            is ViewState.Success<*> -> {
                Log.d(getFragmentTag(), it.data.toString())
                dataAdapter.update(it.data as List<DoctorModel>)
            }
            is ViewState.Error<*> -> {
                Log.d(getFragmentTag(), it.error.toString())
            }
            is ViewState.Loading -> {
                Log.d(getFragmentTag(), "LOADING")
            }
        }
    }

    private val listener = object : OnItemClickListener<DoctorModel>{
        override fun onClickItem(position: Int, data: DoctorModel) {
            findNavController().navigate(R.id.to_doctor_fragmet, bundleOf("doctor" to data, "title" to data.name))
        }

    }
    private val dataAdapter = object : ItemsListAdapter<DoctorModel>(listener) {
        override fun getLayoutId(position: Int, obj: DoctorModel): Int = R.layout.card_doctor

    }

    override fun onResume() {
        super.onResume()
        viewModel.viewState.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
        viewModel.viewState.removeObserver(dataRetriever)
        super.onPause()
    }

    override fun onDestroyView() {
        hospitalBinding = null
        super.onDestroyView()
    }

}