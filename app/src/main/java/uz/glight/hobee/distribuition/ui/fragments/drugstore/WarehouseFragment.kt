package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.content.Intent
import android.os.Bundle
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
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import uz.glight.hobee.distribuition.ui.activities.LoginActivity
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.AmountDialog
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.PositiveNegativeCallback
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.viewmodels.WareHouseViewModel
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    lateinit var adapter: WareHouseAdapter
    lateinit var wareHouseViewModel: WareHouseViewModel
    var intervall = 0L
    private var param1: String? = null
    lateinit var networkHelper: NetworkHelper
    private var param2: String? = null
    private var bindingBasket: FragmentWarehouseBinding? = null
    private lateinit var db: AppDataBase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        wareHouseViewModel = ViewModelProviders.of(this)[WareHouseViewModel::class.java]
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
                lifecycleScope.launch {
                    var res = RemoteRepository.getDiscounts()
                    if (res.code() > 400) {
                        Snackbar.make(
                            view!!,
                            "You access is failed , please re-enter again",
                            Snackbar.LENGTH_LONG
                        ).show()
                        val la = Intent(requireContext(), LoginActivity::class.java)
                        la.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(la)
                        BottomNavigationActivity().finish()
                    }
                }
                if (query != null) {
                    var cTime = System.currentTimeMillis()
                    if (cTime - intervall > 400) {
                        intervall = cTime
                        wareHouseViewModel.getData(query).observe(viewLifecycleOwner, {
                            lifecycleScope.launch {
                                if (it != null) {
                                    adapter.submitData(it)
                                }

                            }
                        })
                        bindingBasket?.listView?.scrollToPosition(0)
                    }

                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, menuInflater)
    }


    val callback = object : PositiveNegativeCallback {
        override fun positive(bkItem: CreateOrderModel.DrugsListItem) {
            var where_house_id = requireParentFragment().arguments?.getInt("drugstore_id") ?: 0
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
}