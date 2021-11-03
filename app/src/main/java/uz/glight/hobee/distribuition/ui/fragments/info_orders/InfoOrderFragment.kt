package uz.glight.hobee.distribuition.ui.fragments.info_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.glight.hobeedistribuition.network.model.OrderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.OrderMedsAdapter
import uz.glight.hobee.distribuition.databinding.FragmentInfoOrderBinding
import uz.glight.hobee.distribuition.network.models.Item
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
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
        loadData()
        return root
    }

    private fun loadData() {
        try {
                var res = RemoteRepository
                    .getRespons(
                        "http://83.69.136.134/v1/api/request-agent" +
                                "-items?request_agent_id=${param1?.id}"
                    )
                res.enqueue(object : Callback<List<Item>> {
                    override fun onResponse(
                        call: Call<List<Item>>,
                        response: Response<List<Item>>
                    ) {
                        if (response.isSuccessful) {
                            binding?.apply {
                                sum.text = param1?.totalPaymentSum
                                discount.text = param1?.discount
                                paymentType.text = when (param1?.paymentType) {
                                    "transfer" -> getString(R.string.payment_transfer)
                                    "cash" -> getString(R.string.payment_cash)
                                    "terminal" -> getString(R.string.payment_terminal)
                                    else -> getString(R.string.unknown)
                                }
                            }
                            adapter.submitList(response.body())
                        }
                    }

                    override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                        lifecycleScope.launch {
                            Toast.makeText(
                                requireContext(),
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            binding.rv.adapter = adapter
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