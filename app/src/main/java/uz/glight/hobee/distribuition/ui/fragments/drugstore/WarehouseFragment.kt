package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.adapters.ViewHolderFactory
import uz.glight.hobee.distribuition.databinding.FragmentBasketBinding
import uz.glight.hobee.distribuition.databinding.FragmentWarehouseBinding
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.AmountDialog
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.PositiveNegativeCallback
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import java.text.FieldPosition

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var adapter: ItemsListAdapter<DrugModel>
    private var bindingBasket: FragmentWarehouseBinding? = null
    private val viewModel: DrugStoreViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWarehouseBinding.bind(view)
        bindingBasket = binding

        adapter = dataAdapter
        binding.listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
//        viewModel.datamodelWarehouse.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
        lifecycleScope.launch(){
            viewModel.datamodelWarehouse.collect{
                when (it) {
                    is ViewState.Success<*> -> {
                        dataAdapter.update(it.data as List<DrugModel>)
                    }
                    is ViewState.Error<*> -> {
                    }
                    is ViewState.Loading -> {
                    }
                }
            }
        }
    }

    override fun onPause() {
//        viewModel.datamodelWarehouse.removeObserver(dataRetriever)
        super.onPause()
    }

    private val dataRetriever = Observer<ViewState> {
        when (it) {
            is ViewState.Success<*> -> {
//                Log.d(getFragmentTag(), it.data.toString())
                dataAdapter.update(it.data as List<DrugModel>)
            }
            is ViewState.Error<*> -> {
//                Log.d(getFragmentTag(), it.error.toString())
            }
            is ViewState.Loading -> {
//                Log.d(getFragmentTag(), "LOADING")
            }
        }
    }

    val callback = object : PositiveNegativeCallback{
        override fun positive(bkItem: CreateOrderModel.DrugsListItem) {
            viewModel.addDrugToBasket(bkItem)
        }

        override fun negative() {
            // TODO: do something if need
        }

    }
    val listener = object : OnItemClickListener<DrugModel> {
        override fun onClickItem(position: Int, data: DrugModel) {
            val dialog = AmountDialog.newInstance(data, callback)
            dialog.show(childFragmentManager, getFragmentTag())
        }
    }

    private val dataAdapter = object : ItemsListAdapter<DrugModel>(listener) {
        override fun getLayoutId(position: Int, obj: DrugModel): Int = R.layout.card_drug
    }
}