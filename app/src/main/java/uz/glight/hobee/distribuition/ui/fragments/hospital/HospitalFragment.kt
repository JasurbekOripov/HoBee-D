package uz.glight.hobee.distribuition.ui.fragments.hospital

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.DoctorModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.DoctorsAdapter
import uz.glight.hobee.distribuition.databinding.FragmentHospitalBinding
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.internetError
import uz.glight.hobee.distribuition.viewmodels.DoctorsViewModel
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

class HospitalFragment : Fragment(R.layout.fragment_hospital) {

    //    private val viewModel: HospitalViewModel by viewModels()
    private lateinit var viewModel: DoctorsViewModel
    lateinit var adapter: DoctorsAdapter
    private var hospitalBinding: FragmentHospitalBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHospitalBinding.bind(view)
        hospitalBinding = binding
        viewModel = ViewModelProvider(this)[DoctorsViewModel::class.java]
        adapter = DoctorsAdapter(object : DoctorsAdapter.setOnClick {
            override fun itemClick(item: DoctorModel, position: Int) {
                findNavController().navigate(R.id.doctorFragment, bundleOf(Pair("doctor", item)))
            }
        })
        binding.listView.adapter = adapter
        val clinic = requireArguments().getSerializable("clinic") as ClinicModel
        binding.extendedFab.setOnClickListener {
            findNavController().navigate(
                R.id.to_discussion_list,
                bundleOf(Pair("clinic_id", clinic.id.toString()))
            )
        }
    }


    private fun loadData() {
        val clinic = requireArguments().getSerializable("clinic") as ClinicModel
        viewModel.getData(clinic.id.toString(), "").observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

    }
//    private val dataRetriever = Observer<ViewState> {
//        when (it) {
//            is ViewState.Success<*> -> {
//                Log.d(getFragmentTag(), it.data.toString())
//                dataAdapter.update(it.data as List<DoctorModel>)
//            }
//            is ViewState.Error<*> -> {
//                Log.d(getFragmentTag(), it.error.toString())
//            }
//            is ViewState.Loading -> {
//                Log.d(getFragmentTag(), "LOADING")
//            }
//        }
//    }

//    private val listener = object : OnItemClickListener<DoctorModel> {
//        override fun onClickItem(position: Int, data: DoctorModel) {
//            findNavController().navigate(
//                R.id.to_doctor_fragmet,
//                bundleOf("doctor" to data, "title" to data.name)
//            )
//        }

//    }
//    private val dataAdapter = object : ItemsListAdapter<DoctorModel>(listener) {
//        override fun getLayoutId(position: Int, obj: DoctorModel): Int = R.layout.card_doctor
//
//    }

    override fun onResume() {
        super.onResume()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            view?.let { it.internetError() }
        }
//        viewModel.viewState.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
//        viewModel.viewState.removeObserver(dataRetriever)
        super.onPause()
    }

    override fun onDestroyView() {
        hospitalBinding = null
        super.onDestroyView()
    }

}