package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.glight.hobee.distribuition.databinding.CardDoctorBinding
import uz.glight.hobee.distribuition.databinding.CardPharmBinding
import uz.glight.hobee.distribuition.network.models.ClinicModel

class ClinicAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<ClinicModel, ClinicAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardPharmBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: ClinicModel, position: Int) {
            item.apply {
                cardTitle.text = data.name
                address.text = data.address
                phone.text = data.receptionPhone
                inn.visibility = View.GONE
                innTitle.visibility = View.GONE
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<ClinicModel>() {
        override fun areItemsTheSame(oldItem: ClinicModel, newItem: ClinicModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ClinicModel,
            newItem: ClinicModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardPharmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: ClinicModel, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}