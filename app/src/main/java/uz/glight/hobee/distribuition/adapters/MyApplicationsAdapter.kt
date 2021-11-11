package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.OrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.CardDocStoryBinding
import uz.glight.hobee.distribuition.databinding.CardDrugBinding
import uz.glight.hobee.distribuition.databinding.CardOrderBinding
import uz.glight.hobee.distribuition.network.models.Item

class MyApplicationsAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<OrderModel, MyApplicationsAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardOrderBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: OrderModel, position: Int) {
            item.apply {
                if (data?.pharmacy!=null){
                    cardTitle.text = data?.pharmacy?.name ?: ""
                    address.text = data.pharmacy.address
                    phone.text = data.pharmacy.receptionPhone
                    price.text = "${data.totalPaymentSum} ${itemView.context.getString(R.string.som)}"
                    when (data.status) {
                        0 -> {
                            status.text = itemView.context.getString(R.string.status_new)
                            status.setBackgroundResource(R.color.green)
                        }
                        1 -> {
                            status.text = itemView.context.getString(R.string.status_finished)
                            status.setBackgroundResource(R.color.green)
                        }
                        2 -> {
                            status.text = itemView.context.getString(R.string.status_canceled)
                            status.setBackgroundResource(R.color.red)
                        }
                    }
                    val remainedPayment = data.remainedPayment
                    if (!remainedPayment.isNullOrEmpty()) {
                        if (remainedPayment.toDouble() > 0) {
                            debtTitle.visibility = View.VISIBLE
                            debt.apply {
                                visibility = View.VISIBLE
                                text = remainedPayment
                            }
                            cardWrapper.setBackgroundResource(R.drawable.rounded_stroke_red_8)
                        }
                    }
                }
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderModel,
            newItem: OrderModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: OrderModel, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}