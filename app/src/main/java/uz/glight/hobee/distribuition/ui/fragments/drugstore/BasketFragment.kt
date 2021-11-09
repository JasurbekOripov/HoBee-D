package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.annotations.SerializedName
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.BasketListAdapter
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentBasketBinding
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.CreateApplicationDialog
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import java.lang.Exception

class BasketFragment : Fragment(R.layout.fragment_basket) {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: BasketListAdapter
    private var bindingBasket: FragmentBasketBinding? = null
    private val viewModel: DrugStoreViewModel by viewModels({ requireParentFragment() })
    private lateinit var dialog: CreateApplicationDialog
    private lateinit var binding: FragmentBasketBinding
    lateinit var db: AppDataBase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        binding = FragmentBasketBinding.bind(view)
        bindingBasket = binding
        db = AppDataBase.getInstanse(requireContext())
    }

//    override fun onStart() {
//        super.onStart()
//        loadData()
//    }

    private fun loadData() {
        val parentArgs = requireParentFragment().arguments
        val userData =
            ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)
        adapter = BasketListAdapter()
        binding.listView.adapter = adapter
        var where_house_id = parentArgs?.getInt("drugstore_id") ?: 0
        var list = db.dao().getMedsByWhereHouse(where_house_id)
        adapter.submitList(list)
        var listOfDrugs = ArrayList<CreateOrderModel.DrugsListItem>()
        var allPrice = 0.0
        try {
            for (i in list) {
                allPrice += i.allPrice
                listOfDrugs.add(
                    CreateOrderModel.DrugsListItem(
                        name = i.name,
                        warehouseId = i.warehouseId,
                        id = i.pharmacy_id,
                        priceForOne = i.priceForOne,
                        amount = i.amount,
                        allPrice = i.allPrice
                    )
                )
            }
        } catch (e: Exception) {
            Snackbar.make(requireView(), "Creating error", Snackbar.LENGTH_SHORT).show()
        }
        binding.goToCreating.setOnClickListener {
            if (list.isNotEmpty()) {
                val createData = CreateOrderModel(
                    id = where_house_id,
                    agentId = userData?.id,
                    generalPrice = allPrice.toString(),
                    sale = null,
                    prepayment = null,
                    paymentType = "",
                    listOfDrugs = listOfDrugs
                )
                findNavController().popBackStack()
                findNavController().navigate(
                    R.id.createApplicationFragment,
                    bundleOf("data" to createData)
                )
            } else {
                Snackbar.make(
                    view ?: View(requireContext()),
                    "Basket is empty",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun createCallback() {
        dialog.dismiss()
    }


    override fun onResume() {
        super.onResume()
        loadData()
        viewModel.datamodelBasket.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
        viewModel.datamodelBasket.removeObserver(dataRetriever)
        super.onPause()
    }

    private val dataRetriever = Observer<List<CreateOrderModel.DrugsListItem>> {

//            it as List<CreateOrderModel.DrugsListItem>

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val dataAdapter = object : ItemsListAdapter<CreateOrderModel.DrugsListItem>(null) {
        override fun getLayoutId(position: Int, obj: CreateOrderModel.DrugsListItem): Int =
            R.layout.card_backet_item
    }
}