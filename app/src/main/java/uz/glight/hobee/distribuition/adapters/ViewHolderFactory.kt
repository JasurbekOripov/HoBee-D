package uz.glight.hobee.distribuition.adapters

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.glight.hobeedistribuition.network.model.*
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.utils.OnItemClickListener

object ViewHolderFactory {

    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.card_backet_item -> BasketViewHolder(view)
            R.layout.card_pharm -> ClinicPharmacyVH(view)
            R.layout.card_drug -> WarehouseViewHolder(view)
            R.layout.card_doctor -> DoctorsViewHolder(view)
            R.layout.card_doc_story -> DiscussionViewHolder(view)
            R.layout.card_order -> ApplicationViewHolder(view)
            else -> throw Exception("Cannot found assignable View Holder class. File: ViewHolderFactory.kt")
        }
    }

    class ApplicationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), ItemsListAdapter.Binder<OrderModel>{
        val card: ConstraintLayout = itemView.findViewById(R.id.card_wrapper)
        val title: TextView = itemView.findViewById(R.id.card_title)
        val address: TextView = itemView.findViewById(R.id.address)
        val status: TextView = itemView.findViewById(R.id.status)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val totalSum: TextView = itemView.findViewById(R.id.price)
        val debtTitle: TextView = itemView.findViewById(R.id.debt_title)
        val debtSum: TextView = itemView.findViewById(R.id.debt)
        override fun bind(
            position: Int,
            data: OrderModel,
            listener: OnItemClickListener<OrderModel>?
        ) {
            title.text = data.pharmacy?.name ?: ""
            address.text = data.pharmacy?.address
            phone.text = data.pharmacy?.receptionPhone

            totalSum.text = "${data.totalPaymentSum} ${itemView.context.getString(R.string.som)}"
            when(data.status){
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
            if (!remainedPayment.isNullOrEmpty()){
                if (remainedPayment.toDouble() > 0){
                    debtTitle.visibility = View.VISIBLE
                    debtSum.apply {
                        visibility = View.VISIBLE
                        text = remainedPayment
                    }
                    card.setBackgroundResource(R.drawable.rounded_stroke_red_8)
                }
            }
            listener?.let {
                itemView.setOnClickListener {
                    listener.onClickItem(position, data)
                }
            }
        }


    }

    class DiscussionViewHolder(view: View) : RecyclerView.ViewHolder(view),
        ItemsListAdapter.Binder<DiscussionModel> {
        val docName: TextView = view.findViewById(R.id.dc_name)
        val date: TextView = view.findViewById(R.id.discussion_date)
        val time: TextView = view.findViewById(R.id.discussion_time)

        override fun bind(
            position: Int,
            data: DiscussionModel,
            listener: OnItemClickListener<DiscussionModel>?
        ) {
            docName.text = data.doctor?.fullname ?: ""
            val d: String = data.discussionDate.toString()
            val dateTime = d.split(" ")
            date.text = dateTime[0] ?: ""
            time.text = dateTime[1] ?: ""
        }
    }


    class DoctorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemsListAdapter.Binder<DoctorModel> {
        val textView: TextView = itemView.findViewById(R.id.dc_name)
        val phone: TextView = itemView.findViewById(R.id.dc_phone)
        val specialization: TextView = itemView.findViewById(R.id.spicialization)
        override fun bind(
            position: Int,
            data: DoctorModel,
            listener: OnItemClickListener<DoctorModel>?
        ) {
            textView.text = data.name
            if (data.specialization?.isNotEmpty() == true) {
                var specs = ""
                data.specialization.forEach {
                    specs += it.name
                }
                specialization.text = specs
            } else {
                specialization.text = "Не указано"
            }
            phone.text = "+${data.phone}"
            itemView.setOnClickListener {
                listener?.let {
                    listener.onClickItem(position, data)
                }
            }
        }

    }

    class WarehouseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemsListAdapter.Binder<DrugModel> {
        val textView: TextView = itemView.findViewById(R.id.card_title)
        val country: TextView = itemView.findViewById(R.id.country)
        val form: TextView = itemView.findViewById(R.id.form)
        val type: TextView = itemView.findViewById(R.id.type)
        val price: TextView = itemView.findViewById(R.id.price)

        val uncheckedView: View = itemView.findViewById(R.id.unselected)
        val checkedView: View = itemView.findViewById(R.id.selected)

        override fun bind(
            position: Int,
            data: DrugModel,
            listener: OnItemClickListener<DrugModel>?
        ) {
            textView.text = data.catalogueMedicine?.name
            country.text = data.catalogueMedicine?.countryRel?.name
            form.text = data.catalogueMedicine?.dispensingFormRel?.name
            type.text = data.catalogueMedicine?.dosageFormRel?.name
            price.text = data.sellPrice

            itemView.apply {
                listener?.let {
                    setOnClickListener {
                        listener.onClickItem(position, data).let {
                            if (data.isChecked) {
                                uncheckedView.visibility = View.GONE
                                checkedView.visibility = View.VISIBLE
                            } else {
                                uncheckedView.visibility = View.VISIBLE
                                checkedView.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemsListAdapter.Binder<CreateOrderModel.DrugsListItem> {
        val textView: TextView = itemView.findViewById(R.id.card_title)
        val price: TextView = itemView.findViewById(R.id.price)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val totalPrice: TextView = itemView.findViewById(R.id.amount_price)
        override fun bind(
            position: Int,
            data: CreateOrderModel.DrugsListItem,
            listener: OnItemClickListener<CreateOrderModel.DrugsListItem>?
        ) {
            textView.text = data.name
            price.text = data.priceForOne
            amount.text = data.amount.toString()
            totalPrice.text = "${data.allPrice} ${itemView.context.getString(R.string.som)}"
            itemView.apply {
                listener?.let {
                    setOnClickListener { listener.onClickItem(position, data) }
                }
            }
        }
    }

    class ClinicPharmacyVH(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemsListAdapter.Binder<ClinicModel> {
        val name: TextView = itemView.findViewById(R.id.card_title)
        val address: TextView = itemView.findViewById(R.id.address)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val innTitle: TextView = itemView.findViewById(R.id.inn_title)
        val inn: TextView = itemView.findViewById(R.id.inn)
        override fun bind(
            position: Int,
            data: ClinicModel,
            listener: OnItemClickListener<ClinicModel>?
        ) {
            name.text = data.name
            address.text = data.address
            phone.text = data.receptionPhone
            inn.visibility = View.GONE
            innTitle.visibility = View.GONE
            itemView.apply {
                listener?.let {
                    setOnClickListener { listener.onClickItem(position, data) }
                }
            }
        }
    }
}