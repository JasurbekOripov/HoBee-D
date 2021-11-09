package uz.glight.hobee.distribuition.ui.fragments.drugstore

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.utils.ArrayListUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.ulugbek.ibragimovhelpers.helpers.commons.toast
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentCreateApplicationBinding
import uz.glight.hobee.distribuition.network.models.DiscountModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.room.AppDataBase
import uz.glight.hobee.ibrogimov.commons.getFragmentTag
import uz.glight.hobee.ibrogimov.commons.parseError


class CreateApplicationFragment : Fragment(R.layout.fragment_create_application) {
    private var createAppBinding: FragmentCreateApplicationBinding? = null
    private lateinit var corJob: CoroutineScope
    private lateinit var data: CreateOrderModel
    private var discountsList =
        MutableLiveData<List<DiscountModel>>(listOf(DiscountModel(0, "0", "0", 0, "0", 0, null, 0)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            data = getSerializable("data") as CreateOrderModel
        }
        corJob = CoroutineScope(Job() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        corJob.launch {
            val response = RemoteRepository.getDiscounts()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    discountsList.value =
                        ArrayListUtils.merge(discountsList.value!!, response.body()!!)
                }
            }
        }
        val binding = FragmentCreateApplicationBinding.bind(view)
        createAppBinding = binding
        createAppBinding?.apply {
            cartAllPrice.text = data.generalPrice
            var payment = "cash"
            paymentType.setOnCheckedChangeListener { radioGroup, i ->
                payment = when (i) {
                    R.id.cash -> "cash"
                    R.id.terminal -> "terminal"
                    R.id.transfer -> "transfer"
                    else -> "cash"
                }
            }
            data.paymentType = payment
            data.sale = "0.0"
            data.prepayment = "0.0"
            closeModal.setOnClickListener {
                findNavController().navigateUp()
            }
            discounts.check(0)
            createOrder.setOnClickListener {
                Log.d(getFragmentTag(), "onViewCreated: $data")
                corJob.launch {
                    val response = RemoteRepository.createApplication(data)
                    if (response.isSuccessful) {
                        var dao = AppDataBase.getInstanse(requireContext()).dao()
                        dao.deleteMedsByWhereHouse(data.listOfDrugs?.get(0)?.id ?: 1)
                        context?.toast("Создан")
                        lifecycleScope.launch(Dispatchers.Main) {
                            findNavController().popBackStack()
                        }
                    } else {
                        val errorBody = parseError(response)
                        context?.toast("${errorBody.message} ${errorBody.status}")
                    }
                }

            }
            discounts.setOnCheckedChangeListener { group, checkedId ->
                val text = group.findViewById<Chip>(checkedId).text.toString()
                val disPrepInfo = text.split("/")
                data.sale = disPrepInfo.get(0)
                data.prepayment = disPrepInfo.get(1)
            }
        }

        discountsList.observe(viewLifecycleOwner, chipObservable)
    }


    val chipObservable = Observer<List<DiscountModel>> {

        createAppBinding?.discounts?.removeAllViews()
        var id = 0
        it.forEach {
            val chip = Chip(createAppBinding?.discounts?.context);
            chip.id = View.generateViewId()
            chip.isCheckable = true
            if (id == 0) {
                id = View.generateViewId()
            }
            chip.text =
                "${(it.discount.toDouble() * 10.0) / 10.0} / ${(it.prepayment.toDouble() * 10.0) / 10.0}";
            if (chip.text == "0.0 / 0.0") {
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
    }


    override fun onDestroyView() {
        createAppBinding = null
        corJob.cancel()
        discountsList.removeObserver(chipObservable)
        super.onDestroyView()
    }
}