package uz.glight.hobee.distribuition.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import uz.glight.hobee.distribuition.R

class SpinnerAdapter(var context: Context, var list: ArrayList<String>) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any? {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val estado: String = list.get(position)
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.spinner_item, parent, false)
            viewHolder.llEstadoCidades = convertView.findViewById(R.id.root_layout) as LinearLayout
            viewHolder.tvEstadoCidade = convertView.findViewById(R.id.spinner_item_tv) as TextView
            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.tvEstadoCidade?.text = estado

        return convertView
    }

    //holder pattern
    private class ViewHolder {
        var llEstadoCidades: LinearLayout? = null
        var tvEstadoCidade: TextView? = null
    }
}