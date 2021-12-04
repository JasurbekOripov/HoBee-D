package uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentCreateApplicationBinding
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.utils.internetError
import java.lang.Exception


class CreateApplicationDialog(private val data: CreateOrderModel, private val create: () -> Unit) :
    BottomSheetDialogFragment() {
    private var createAppBinding: FragmentCreateApplicationBinding? = null
    private lateinit var corJob: CoroutineScope

    companion object {
        fun newInstance(createData: CreateOrderModel, create: () -> Unit) =
            CreateApplicationDialog(createData, create)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateApplicationBinding.inflate(inflater, container, false)
        createAppBinding = binding
        corJob = CoroutineScope(Job() + Dispatchers.IO)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
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
                    data.paymentType = payment
                }
                closeModal.setOnClickListener {
                    dismiss()
                }
                createOrder.setOnClickListener {
                    try {
                    if (NetworkHelper(requireContext()).isNetworkConnected()){
                        corJob.launch {
                            val response = RemoteRepository.createApplication(data)
                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    create.invoke()
                                }
                            }
                        }
                    }

                    } catch (e: Exception) {

                    }
                }
            }
        } else {
            view.internetError()
        }
    }


    override fun onDestroyView() {
        createAppBinding = null
        corJob.cancel()
        super.onDestroyView()
    }
}