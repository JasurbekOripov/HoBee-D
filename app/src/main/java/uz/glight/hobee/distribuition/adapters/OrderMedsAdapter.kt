package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.glight.hobee.distribuition.databinding.CardDrugBinding
import uz.glight.hobee.distribuition.network.models.Item

class OrderMedsAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<Item, OrderMedsAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardDrugBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: Item, position: Int) {
            item.apply {
                price.text = data.price
                cardTitle.text = data.catalogueMedicineName
                country.text = data.catalogueMedicineCountry
                form.text = data.catalogueMedicineDispensingForm
                type.text = data.catalogueMedicineDosageForm
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardDrugBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: Item, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}