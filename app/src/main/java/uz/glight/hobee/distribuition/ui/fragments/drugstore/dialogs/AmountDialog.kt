package uz.glight.hobee.distribuition.ui.fragments.drugstore.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.DialogAmountBinding
import uz.glight.hobee.distribuition.ui.fragments.drugstore.DrugStoreViewModel

class AmountDialog(private val callback: PositiveNegativeCallback) : BottomSheetDialogFragment() {
    private var amountBinding: DialogAmountBinding? = null
    private lateinit var drug: DrugModel

    companion object {
        fun newInstance(drug: DrugModel, callback: PositiveNegativeCallback) = AmountDialog(callback).apply {
            arguments = Bundle().apply {
                putSerializable("drug", drug)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogAmountBinding.inflate(inflater, container, false)
        amountBinding = binding
        drug = requireArguments().getSerializable("drug") as DrugModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amountBinding?.apply {
            dialogTitle.text = drug.catalogueMedicine.name
            val editTextVal = amoutOfDrug.text
            positiveBtn.setOnClickListener {
                if (editTextVal.isNotBlank() && editTextVal.isNotEmpty()){
                    val totalPrice = drug.sellPrice.toDouble() * editTextVal.toString().toInt()
                    val bkItem: CreateOrderModel.DrugsListItem = CreateOrderModel.DrugsListItem(
                        drug.catalogueMedicine.name,
                        drug.id,
                        drug.catalogueMedicine.id,
                        drug.sellPrice,
                        amount = editTextVal.toString().toInt(),
                        totalPrice
                    )
                    callback.positive(bkItem)
                    dismiss()
                } else {
                    amoutOfDrug.error = ""
                }
            }
            negativeBtn.setOnClickListener {
                dismiss()
                callback.negative()
            }
        }
    }

    override fun onDestroyView() {
        amountBinding = null
        super.onDestroyView()
    }

}

interface PositiveNegativeCallback {
    fun positive(bkItem: CreateOrderModel.DrugsListItem)
    fun negative()
}
