package uz.glight.hobee.distribuition.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.glight.hobee.distribuition.utils.GenericDiffUtil
import uz.glight.hobee.distribuition.utils.GenericItemDiff
import uz.glight.hobee.distribuition.utils.OnItemClickListener

/**
 * Generic List Adapter
 */
abstract class ItemsListAdapter<T>(listener: OnItemClickListener<T>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList = mutableListOf<T>()
    private var itemClickListener: OnItemClickListener<T>? = listener
    private var diffUtil: GenericItemDiff<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            , viewType
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder as Binder<T>).bind(position, itemList[position], itemClickListener,)
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = getLayoutId(position, itemList[position])

    fun setDiffUtilCallback(diffUtilImpl: GenericItemDiff<T>) {
        diffUtil = diffUtilImpl
    }

    fun update(items: List<T>) {
        if (diffUtil != null) {
            val result = DiffUtil.calculateDiff(GenericDiffUtil(itemList, items, diffUtil!!))

            itemList.clear()
            itemList.addAll(items)
            result.dispatchUpdatesTo(this)
        } else {
            itemList = items.toMutableList()
            notifyDataSetChanged()
        }
    }

    protected abstract fun getLayoutId(position: Int, obj: T): Int

    protected open fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(view, viewType)
    }

    internal interface Binder<T> {
        fun bind(position: Int, data: T, listener: OnItemClickListener<T>?)
    }
}