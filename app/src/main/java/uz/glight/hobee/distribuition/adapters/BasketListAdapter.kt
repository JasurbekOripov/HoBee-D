package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.glight.hobee.distribuition.databinding.CardBacketItemBinding
import uz.glight.hobee.distribuition.room.entity.SavedMedEntity

class BasketListAdapter(var onClick: setOnClick) :
    ListAdapter<SavedMedEntity, BasketListAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemBinding: CardBacketItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(data: SavedMedEntity, position: Int) {
            itemBinding.apply {
                cardTitle.text = data.name
                price.text = data.priceForOne
                amount.text = data.amount.toString()
                amountPrice.text = data.allPrice.toString()
            }
            itemBinding.root.setOnClickListener {
                onClick.itemClicked(data, position)
            }
        }
    }

    class MyDiffUtil() : DiffUtil.ItemCallback<SavedMedEntity>() {
        override fun areItemsTheSame(oldItem: SavedMedEntity, newItem: SavedMedEntity): Boolean {
            return oldItem.saved_med_id == newItem.saved_med_id
        }

        override fun areContentsTheSame(oldItem: SavedMedEntity, newItem: SavedMedEntity): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardBacketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }

    interface setOnClick {
        fun itemClicked(savedMedEntity: SavedMedEntity, position: Int)
    }
}