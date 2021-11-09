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
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.AmountDialog
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.PositiveNegativeCallback
import uz.glight.hobee.distribuition.utils.OnItemClickListener
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import java.text.FieldPosition
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var adapter: ItemsListAdapter<DrugModel>

    private var param1: String? = null
    private var param2: String? = null
    private var bindingBasket: FragmentWarehouseBinding? = null
    private val viewModel: DrugStoreViewModel by viewModels({ requireParentFragment() })
    private lateinit var db: AppDataBase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val binding = FragmentWarehouseBinding.bind(view)
        bindingBasket = binding
        db = AppDataBase.getInstanse(requireContext())
        adapter = dataAdapter
        binding.listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
//        viewModel.datamodelWarehouse.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
        lifecycleScope.launch() {
            viewModel.datamodelWarehouse.collect {
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

//    override fun onPause() {
////        viewModel.datamodelWarehouse.removeObserver(dataRetriever)
//        super.onPause()
//    }

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

    val callback = object : PositiveNegativeCallback {
        override fun positive(bkItem: CreateOrderModel.DrugsListItem) {
            var where_house_id = requireParentFragment().arguments?.getInt("drugstore_id")?:0
//            viewModel.addDrugToBasket(bkItem)
            db.dao().add(
                SavedMedEntity(
                    med_id = bkItem.id ?: 1,
                    warehouseId = bkItem.warehouseId ?: 1,
                    pharmacy_id=where_house_id,
                    priceForOne = bkItem.priceForOne.toString(),
                    name = bkItem.name.toString(),
                    amount = bkItem.amount ?: 0,
                    allPrice = bkItem.allPrice
                )
            )

        }

        override fun negative() {

        }

    }
    val listener = object : OnItemClickListener<DrugModel> {
        override fun onClickItem(position: Int, data: DrugModel) {
            val dialog = AmountDialog.newInstance(data, callback)
            dialog.show(childFragmentManager, getFragmentTag())
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WarehouseFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
    private val dataAdapter = object : ItemsListAdapter<DrugModel>(listener) {
        override fun getLayoutId(position: Int, obj: DrugModel): Int = R.layout.card_drug
    }
}