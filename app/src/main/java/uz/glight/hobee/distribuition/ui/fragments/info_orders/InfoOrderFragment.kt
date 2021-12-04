package uz.glight.hobee.distribuition.ui.fragments.info_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.glight.hobeedistribuition.network.model.OrderModel
import com.google.android.material.snackbar.Snackbar
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.OrderMedsAdapter
import uz.glight.hobee.distribuition.databinding.FragmentInfoOrderBinding
import uz.glight.hobee.distribuition.network.models.Item
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.internetError
import uz.glight.hobee.distribuition.viewmodels.OrderItemsViewModel
import java.lang.Exception

private const val ARG_PARAM1 = "data"
private const val ARG_PARAM2 = "param2"

class InfoOrderFragment : Fragment() {
    lateinit var binding: FragmentInfoOrderBinding
    private var param1: OrderModel? = null
    private var param2: String? = null
    lateinit var adapter: OrderMedsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as OrderModel
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoOrderBinding.inflate(inflater, container, false)
        var root = binding.root
        adapter = OrderMedsAdapter(object : OrderMedsAdapter.setOnClick {
            override fun itemClick(item: Item, position: Int) {

            }
        })
        binding.rv.adapter = adapter
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            loadData()
        } else {
            view?.let { it.internetError() }
        }
        return root
    }

    private fun loadData() {
        try {
            binding.apply {
                sum.text = param1?.totalPaymentSum
                discount.text = param1?.discount
                paymentType.text = when (param1?.paymentType) {
                    "transfer" -> getString(R.string.payment_transfer)
                    "cash" -> getString(R.string.payment_cash)
                    "terminal" -> getString(R.string.payment_terminal)
                    else -> getString(R.string.unknown)
                }
            }
            var viewmodel = ViewModelProvider(this)[OrderItemsViewModel::class.java]
            viewmodel.getData(param1?.id ?: 1).observe(viewLifecycleOwner, {
                adapter.submitData(lifecycle, it)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}