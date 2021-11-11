package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.DoctorModel
import uz.glight.hobee.distribuition.databinding.CardDocStoryBinding
import uz.glight.hobee.distribuition.databinding.CardDoctorBinding

class DoctorsAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<DoctorModel, DoctorsAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardDoctorBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: DoctorModel, position: Int) {
            item.apply {
                dcName.text = data.name
                if (data.specialization?.isNotEmpty() == true) {
                    var specs = ""
                    data.specialization.forEach {
                        specs += it.name
                    }
                    spicialization.text = specs
                } else {
                    spicialization.text = "Не указано"
                }
                dcPhone.text = "+${data.phone}"
                itemView.setOnClickListener {
                }
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<DoctorModel>() {
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DoctorModel,
            newItem: DoctorModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: DoctorModel, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}