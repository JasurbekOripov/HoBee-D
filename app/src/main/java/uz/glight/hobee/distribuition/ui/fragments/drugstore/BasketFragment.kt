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
import uz.glight.hobee.distribuition.adapters.ItemsListAdapter
import uz.glight.hobee.distribuition.databinding.FragmentBasketBinding
import uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs.CreateApplicationDialog
import uz.glight.hobee.ibrogimov.commons.getFragmentTag

class BasketFragment : Fragment(R.layout.fragment_basket) {
    private lateinit var adapter: ItemsListAdapter<CreateOrderModel.DrugsListItem>
    private var bindingBasket: FragmentBasketBinding? = null
    private val viewModel: DrugStoreViewModel by viewModels({ requireParentFragment() })
    private lateinit var dialog: CreateApplicationDialog
    private lateinit var binding: FragmentBasketBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBasketBinding.bind(view)
        bindingBasket = binding
        loadData()
    }

    private fun loadData() {
        val parentArgs = requireParentFragment().arguments
        val userData =
            ModelPreferencesManager.get<UserModel>(ModelPreferencesManager.PREFERENCES_FILE_NAME)
        binding.listView.adapter = dataAdapter
        binding.goToCreating.setOnClickListener {
            if (dataAdapter.itemCount != 0) {
                val createData = CreateOrderModel(
                    id = parentArgs?.getInt("drugstore_id"),
                    agentId = userData?.id,
                    generalPrice = viewModel.generalPrice.value.toString(),
                    sale = null,
                    prepayment = null,
                    paymentType = "",
                    listOfDrugs = viewModel.datamodelBasket.value
                )
                findNavController().popBackStack()
                findNavController().navigate(R.id.createApplicationFragment, bundleOf("data" to createData))
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
        viewModel.datamodelBasket.observe(requireParentFragment().viewLifecycleOwner, dataRetriever)
    }

    override fun onPause() {
        viewModel.datamodelBasket.removeObserver(dataRetriever)
        super.onPause()
    }

    private val dataRetriever = Observer<List<CreateOrderModel.DrugsListItem>> {
        dataAdapter.update(it as List<CreateOrderModel.DrugsListItem>)
    }

    private val dataAdapter = object : ItemsListAdapter<CreateOrderModel.DrugsListItem>(null) {
        override fun getLayoutId(position: Int, obj: CreateOrderModel.DrugsListItem): Int =
            R.layout.card_backet_item
    }
}