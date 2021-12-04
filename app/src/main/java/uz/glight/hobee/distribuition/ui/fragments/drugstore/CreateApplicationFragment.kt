package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import uz.glight.hobee.distribuition.utils.ArrayListUtils
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.ulugbek.ibragimovhelpers.helpers.commons.toast
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.SpinnerAdapter
import uz.glight.hobee.distribuition.databinding.FragmentCreateApplicationBinding
import uz.glight.hobee.distribuition.network.models.DiscountModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import uz.glight.hobee.ibrogimov.commons.parseError
import java.lang.Exception
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast


class CreateApplicationFragment : Fragment(R.layout.fragment_create_application) {
    private var createAppBinding: FragmentCreateApplicationBinding? = null
    private lateinit var corJob: CoroutineScope
    lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var data: CreateOrderModel
    var prePayment = 0F
    var prePaymentList = ArrayList<String>()
    var id: Int? = 0
    private var discountsList =
        MutableLiveData<List<DiscountModel>>(listOf(DiscountModel(0, "0", "0", 0, "0", 0, null, 0)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            data = getSerializable("data") as CreateOrderModel
            id = getInt("id", 0)
        }
        corJob = CoroutineScope(Job() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateApplicationBinding.bind(view)
        createAppBinding = binding
        try {

            if (NetworkHelper(requireContext()).isNetworkConnected()) {
                corJob.launch {
                    val response = RemoteRepository.getDiscounts()
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            discountsList.value =
                                ArrayListUtils.merge(discountsList.value!!, response.body()!!)
                        }
                    }
                }
            } else {
                Snackbar.make(view, "No internet connection", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT).show()
        }
        createAppBinding?.apply {
            cartAllPrice.text = data.generalPrice
            data.paymentType = "cash"
            paymentType.setOnCheckedChangeListener { radioGroup, i ->
                data.paymentType = when (i) {
                    R.id.cash -> "cash"
                    R.id.terminal -> "terminal"
                    R.id.transfer -> "transfer"
                    else -> "cash"
                }
            }

            data.sale = "0.0"
            closeModal.setOnClickListener {
                findNavController().navigateUp()
            }
            discounts.check(0)
            try {

                createOrder.setOnClickListener {
                    if (NetworkHelper(requireContext()).isNetworkConnected()) {
                        try {
                            var price = data.generalPrice?.toFloat()
                            if (data.sale != "0") {
                                var discount =
                                    (price?.times(data.sale?.toFloat() ?: 1F)?.div(100)) ?: 0F
                                price = price?.minus(discount)
                            }
                            if (price != 0F || prePayment < 1) {
                                if (prePayment != 0F) {
                                    var prePaymentSum = (price?.times(prePayment)?.div(100)) ?: 0F
                                   price= price?.minus(prePaymentSum)
                                }
                                data.prepayment=prePayment
                                data.generalPrice = price.toString()
                                Log.d(getFragmentTag(), "onViewCreated: $data")
                                corJob.launch {
                                    val response = RemoteRepository.createApplication(data)
                                    if (response.isSuccessful) {
                                        var dao = AppDataBase.getInstanse(requireContext()).dao()
                                        dao.deleteMedsbyPharmId(id ?: 0)
                                        context?.toast("Создан")
                                        lifecycleScope.launch(Dispatchers.Main) {
                                            findNavController().popBackStack()
                                        }
                                    } else {
                                        val errorBody = parseError(response)
                                        context?.toast("${errorBody.message} ${errorBody.status}")
                                    }
                                }
                            } else {
                                Snackbar.make(
                                    requireView(),
                                    "вы выбрали 100% скидку и не можете " +
                                            "внести предоплату  пожалуйста, сделайте предоплату  0%",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        Snackbar.make(view, "No internet connection", Snackbar.LENGTH_SHORT).show()
                    }


                }

            } catch (e: Exception) {
                Snackbar.make(discounts, "Error", Snackbar.LENGTH_SHORT).show()
            }
            discounts.setOnCheckedChangeListener { group, checkedId ->
                val text = group.findViewById<Chip>(checkedId).text.toString()
                val disPrepInfo = text.split("/")
                data.sale = disPrepInfo.get(0)
            }
        }


        discountsList.observe(viewLifecycleOwner, chipObservable)
    }


    val chipObservable = Observer<List<DiscountModel>> {
        prePaymentList = ArrayList()
        try {
            createAppBinding?.discounts?.removeAllViews()
            var id = 0
            it.forEach {
                val chip = Chip(createAppBinding?.discounts?.context);
                chip.id = View.generateViewId()
                chip.isCheckable = true
                if (id == 0) {
                    id = View.generateViewId()
                }
                var discount =
                    if (it.discount != "0") it.discount.subSequence(0, it.discount.indexOf("."))
                    else "0.0"
                var prePay = if (it.prepayment != "0") it.prepayment.subSequence(
                    0,
                    it.prepayment.indexOf(".")
                ).toString()
                else "0"
                prePaymentList.add(prePay)
                chip.text = discount
                if (chip.text == "0.0") {
                    chip.isSelected = true
                }
                chip.setChipBackgroundColorResource(R.color.blue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chip.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    chip.setTextColor(resources.getColor(R.color.white))
                }
                createAppBinding?.discounts?.addView(chip)
                if (id > 0) {
                    createAppBinding?.discounts?.check(chip.id)
                    id = -1
                }
            }
            spinnerAdapter = SpinnerAdapter(requireContext(), prePaymentList)
            createAppBinding?.prepaySum?.adapter = spinnerAdapter
            createAppBinding?.prepaySum?.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    prePayment = prePaymentList[position].toFloat()
                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        createAppBinding = null
        corJob.cancel()
        discountsList.removeObserver(chipObservable)
        super.onDestroyView()
    }
}