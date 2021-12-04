package uz.glight.hobee.distribuition.ui.fragments.notifications

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.DoctorModel
import com.glight.hobeedistribuition.network.model.OrderModel
import com.google.android.material.snackbar.Snackbar
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.adapters.MyApplicationsAdapter
import uz.glight.hobee.distribuition.databinding.FragmentApplicationsBinding
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.distribuition.utils.internetError
import uz.glight.hobee.distribuition.viewmodels.MyApplicationsViewModel
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

class ApplicationsFragment : Fragment(R.layout.fragment_applications) {
    lateinit var viewModel: MyApplicationsViewModel
    private var applicationsFragmentBinding: FragmentApplicationsBinding? = null
    lateinit var myAdapter: MyApplicationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentApplicationsBinding.bind(view)
        applicationsFragmentBinding = binding
        myAdapter = MyApplicationsAdapter(object : MyApplicationsAdapter.setOnClick {
            override fun itemClick(data: OrderModel, position: Int) {
                findNavController().navigate(R.id.infoOrderFragment, bundleOf("data" to data))
            }
        })
        binding.listView.adapter = myAdapter
    }


    override fun onResume() {
        super.onResume()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            view?.let { it.internetError() }
        }
    }

    private fun loadData() {
        viewModel = ViewModelProvider(this)[MyApplicationsViewModel::class.java]
        viewModel.getData().observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenCreated {
                myAdapter.submitData(it)
            }
        })
    }

    override fun onDestroyView() {
        applicationsFragmentBinding = null
        super.onDestroyView()
    }
}