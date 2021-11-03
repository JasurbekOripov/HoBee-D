package uz.glight.hobee.distribuition.ui.fragments.hospital

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.glight.hobeedistribuition.network.model.*
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentBasketBinding
import uz.glight.hobee.distribuition.databinding.FragmentDiscussionListBinding
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.fragments.drugstore.DrugStoreViewModel
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import uz.glight.hobee.ibrogimov.commons.parseError

class DiscussionListFragment : Fragment(R.layout.fragment_discussion_list) {

    private var bindingDiscussionList: FragmentDiscussionListBinding? = null
    private val viewState = MutableLiveData<ViewState>()
    private var corJob: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        corJob = CoroutineScope(Job() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDiscussionListBinding.bind(view)
        bindingDiscussionList = binding

        binding.listView.adapter = dataAdapter

        corJob?.launch {
            val response = RemoteRepository.getDiscussionList()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main){
                    viewState.postValue(ViewState.Success(data = response.body()!!))
                }
            } else {
                withContext(Dispatchers.Main){
                    viewState.postValue(ViewState.Error<ErrorModel>(error = parseError(response)))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewState.observe(viewLifecycleOwner, dataRetriever)
    }

    override fun onDestroyView(){
        viewState.removeObserver(dataRetriever)
        corJob?.cancel()
        super.onDestroyView()
    }

    private val dataRetriever = Observer<ViewState> {
        when (it) {
            is ViewState.Success<*> -> {
                Log.d(getFragmentTag(), it.data.toString())
                dataAdapter.update(it.data as List<DiscussionModel>)
            }
            is ViewState.Error<*> -> {
                Log.d(getFragmentTag(), it.error.toString())
            }
            is ViewState.Loading -> {
                Log.d(getFragmentTag(), "LOADING")
            }
        }
    }

    private val dataAdapter = object : ItemsListAdapter<DiscussionModel>(null) {
        override fun getLayoutId(position: Int, obj: DiscussionModel): Int = R.layout.card_doc_story
    }
}