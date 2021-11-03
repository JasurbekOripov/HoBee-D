package uz.glight.hobee.distribuition.ui.fragments.notifications

import android.os.Bundle
import android.security.identity.ResultData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.glight.hobeedistribuition.network.model.OrderModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentApplicationInfoBinding
import uz.glight.hobee.distribuition.network.models.Item
import uz.glight.hobee.distribuition.network.models.RequestData
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.lang.Exception

class ApplicationInfoFragment : BottomSheetDialogFragment() {

    private var appInfoBinding: FragmentApplicationInfoBinding? = null
    private lateinit var applicationInfo: OrderModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentApplicationInfoBinding.inflate(inflater, container, false)
        appInfoBinding = binding
        applicationInfo = requireArguments().getSerializable("application_info") as OrderModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appInfoBinding?.apply {
            closeModal.setOnClickListener {
                if (dialog?.isShowing == true) {
                    dismiss()
                }
            }
            totalSum.text = applicationInfo.totalPaymentSum
            orderDiscount.text = applicationInfo.discount
            orderPaymentType.text = when (applicationInfo.paymentType) {
                "transfer" -> getString(R.string.payment_transfer)
                "cash" -> getString(R.string.payment_cash)
                "terminal" -> getString(R.string.payment_terminal)
                else -> getString(R.string.unknown)
            }
        }
    }

    override fun onDestroyView() {
        appInfoBinding = null
        super.onDestroyView()
    }

}