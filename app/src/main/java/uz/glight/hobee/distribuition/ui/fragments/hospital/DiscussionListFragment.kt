package uz.glight.hobee.distribuition.ui.fragments.hospital

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.glight.hobeedistribuition.network.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.DiscussionListAdapter
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.adapters.ViewHolderFactory
import uz.glight.hobee.distribuition.databinding.FragmentDiscussionListBinding
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.internetError
import uz.glight.hobee.distribuition.viewmodels.DiscussionListViewModel
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import uz.glight.hobee.ibrogimov.commons.parseError

class DiscussionListFragment : Fragment(R.layout.fragment_discussion_list) {
    lateinit var adapter: DiscussionListAdapter
    private var bindingDiscussionList: FragmentDiscussionListBinding? = null
//    private val viewState = MutableLiveData<ViewState>()
    private var corJob: CoroutineScope? = null
    lateinit var viewModel: DiscussionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        corJob = CoroutineScope(Job() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDiscussionListBinding.bind(view)
        bindingDiscussionList = binding
        adapter = DiscussionListAdapter(object : DiscussionListAdapter.setOnClick {
            override fun itemClick(item: DiscussionModel, position: Int) {

            }
        })
        binding.listView.adapter = adapter

        viewModel = ViewModelProvider(this)[DiscussionListViewModel::class.java]
        corJob?.launch {
//            viewModel.getData().observe(viewLifecycleOwner, {
//                viewState.postValue(ViewState.Success(data = it))
//            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            view?.let { it.internetError() }
        }
//        viewState.observe(viewLifecycleOwner, dataRetriever)
    }

    private fun loadData() {
        var id=arguments?.getString("clinic_id")
        viewModel.getData(id.toString()).observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
    }

    override fun onDestroyView() {
//        viewState.removeObserver(dataRetriever)
        corJob?.cancel()
        super.onDestroyView()
    }

//    private val dataRetriever = Observer<ViewState> {
//        when (it) {
//            is ViewState.Success<*> -> {
//                Log.d(getFragmentTag(), it.data.toString())
//                dataAdapter.update(it.data as List<DiscussionModel>)
//            }
//            is ViewState.Error<*> -> {
//                Log.d(getFragmentTag(), it.error.toString())
//            }
//            is ViewState.Loading -> {
//                Log.d(getFragmentTag(), "LOADING")
//            }
//        }
//    }

//    private val dataAdapter = object : ItemsListAdapter<DiscussionModel>(null) {
//        override fun getLayoutId(position: Int, obj: DiscussionModel): Int = R.layout.card_doc_story
//    }
}