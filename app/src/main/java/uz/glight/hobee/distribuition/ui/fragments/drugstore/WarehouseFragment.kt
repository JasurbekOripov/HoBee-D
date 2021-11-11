package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.WareHouseAdapter
import uz.glight.hobee.distribuition.databinding.FragmentWarehouseBinding
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.AmountDialog
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.PositiveNegativeCallback
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.viewmodels.WareHouseViewModel
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    //    private lateinit var adapter: ItemsListAdapter<DrugModel>
    lateinit var adapter: WareHouseAdapter
    lateinit var wareHouseViewModel: WareHouseViewModel
    var intervall = 0L

    //    lateinit var drugsMainViewModel: DrugsMainViewModel
    private var param1: String? = null
    lateinit var networkHelper: NetworkHelper
    private var param2: String? = null
    private var bindingBasket: FragmentWarehouseBinding? = null

    //    private val viewModel: DrugStoreViewModel by viewModels({ requireParentFragment() })
    private lateinit var db: AppDataBase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        wareHouseViewModel = ViewModelProviders.of(this)[WareHouseViewModel::class.java]
//        drugsMainViewModel = ViewModelProvider(this)[DrugsMainViewModel::class.java]
        networkHelper = NetworkHelper(requireContext())
        val binding = FragmentWarehouseBinding.bind(view)
        bindingBasket = binding
        setHasOptionsMenu(true)
        db = AppDataBase.getInstanse(requireContext())
        adapter = WareHouseAdapter(object : WareHouseAdapter.setOnClick {
            override fun itemClick(item: DrugModel, position: Int) {
                val dialog = AmountDialog.newInstance(item, callback)
                dialog.show(childFragmentManager, getFragmentTag())
            }
        })
        wareHouseViewModel.getData("").observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

        binding.listView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Поиск"
        searchView.setOnCloseListener {
            if (networkHelper.isNetworkConnected()) {
                wareHouseViewModel.getData("").observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        adapter.submitData(it)
                    }
                })
            } else {
                view?.let {
                    Snackbar.make(it, "No internet connection", Snackbar.LENGTH_SHORT).show()
                }
            }
            false;
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    var cTime = System.currentTimeMillis()
                    if (cTime - intervall > 400) {
                        intervall = cTime
                        wareHouseViewModel.getData(query).observe(viewLifecycleOwner, {
                            lifecycleScope.launch {
                                adapter.submitData(it)
                            }
                        })
                    }

                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onResume() {
        super.onResume()
//        viewModel.datamodelWarehouse.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
//        lifecycleScope.launch() {
//            viewModel.datamodelWarehouse.collect {
//                when (it) {
//                    is ViewState.Success<*> -> {
//                        dataAdapter.update(it.data as List<DrugModel>)
//                    }
//                    is ViewState.Error<*> -> {
//                    }
//                    is ViewState.Loading -> {
//                    }
//                }
//            }
//        }
    }

    private fun loadData() {


    }


//    override fun onPause() {
////        viewModel.datamodelWarehouse.removeObserver(dataRetriever)
//        super.onPause()
//    }

//    private val dataRetriever = Observer<ViewState> {
//        when (it) {
//            is ViewState.Success<*> -> {
////                Log.d(getFragmentTag(), it.data.toString())
//                dataAdapter.update(it.data as List<DrugModel>)
//            }
//            is ViewState.Error<*> -> {
////                Log.d(getFragmentTag(), it.error.toString())
//            }
//            is ViewState.Loading -> {
////                Log.d(getFragmentTag(), "LOADING")
//            }
//        }
//    }

    val callback = object : PositiveNegativeCallback {
        override fun positive(bkItem: CreateOrderModel.DrugsListItem) {
            var where_house_id = requireParentFragment().arguments?.getInt("drugstore_id") ?: 0
//            viewModel.addDrugToBasket(bkItem)
            db.dao().add(
                SavedMedEntity(
                    med_id = bkItem.id ?: 1,
                    warehouseId = bkItem.warehouseId ?: 1,
                    pharmacy_id = where_house_id,
                    priceForOne = bkItem.priceForOne.toString(),
                    name = bkItem.name.toString(),
                    amount = bkItem.amount ?: 0,
                    allPrice = bkItem.allPrice as Long
                )
            )

        }

        override fun negative() {

        }

    }

    //    val listener = object : OnItemClickListener<DrugModel> {
//        override fun onClickItem(position: Int, data: DrugModel) {
//            val dialog = AmountDialog.newInstance(data, callback)
//            dialog.show(childFragmentManager, getFragmentTag())
//        }
//    }
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
//    private val dataAdapter = object : ItemsListAdapter<DrugModel>(listener) {
//        override fun getLayoutId(position: Int, obj: DrugModel): Int = R.layout.card_drug
//    }
}