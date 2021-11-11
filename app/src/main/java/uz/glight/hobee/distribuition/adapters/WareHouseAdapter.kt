package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.DrugModel
import uz.glight.hobee.distribuition.databinding.CardDrugBinding
import uz.glight.hobee.distribuition.network.models.Item

 class WareHouseAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<DrugModel, WareHouseAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardDrugBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: DrugModel, position: Int) {
            item.apply {
                cardTitle.text = data.catalogueMedicine?.name
                country.text = data.catalogueMedicine?.countryRel?.name
                form.text = data.catalogueMedicine?.dispensingFormRel?.name
                type.text = data.catalogueMedicine?.dosageFormRel?.name
                price.text = data.sellPrice
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<DrugModel>() {
        override fun areItemsTheSame(oldItem: DrugModel, newItem: DrugModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DrugModel, newItem: DrugModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardDrugBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: DrugModel, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}