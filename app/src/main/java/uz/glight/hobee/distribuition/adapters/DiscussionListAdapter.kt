package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.DiscussionModel
import com.glight.hobeedistribuition.network.model.DrugModel
import uz.glight.hobee.distribuition.databinding.CardDocStoryBinding
import uz.glight.hobee.distribuition.databinding.CardDrugBinding
import uz.glight.hobee.distribuition.network.models.Item

class DiscussionListAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<DiscussionModel, DiscussionListAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var item: CardDocStoryBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(data: DiscussionModel, position: Int) {
            item.apply {
                dcName.text = data.doctor?.fullname ?: ""
                val d: String = data.discussionDate.toString()
                val dateTime = d.split(" ")
                discussionDate.text = dateTime[0] ?: ""
                discussionTime.text = dateTime[1] ?: ""
            }
            item.root.setOnClickListener {
                itemClick.itemClick(data, position)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<DiscussionModel>() {
        override fun areItemsTheSame(oldItem: DiscussionModel, newItem: DiscussionModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DiscussionModel,
            newItem: DiscussionModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CardDocStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClick(item: DiscussionModel, position: Int)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

}