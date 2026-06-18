package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.BenhAn

class BenhAnAdapter : RecyclerView.Adapter<BenhAnAdapter.ViewHolder>() {

    private var dataList = listOf<BenhAn>()

    fun setData(newList: List<BenhAn>) {
        dataList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_benh_an_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTenBenh = itemView.findViewById<TextView>(R.id.tvTenBenh)
        private val tvMoTa = itemView.findViewById<TextView>(R.id.tvMoTa)
        private val tvDinhKem = itemView.findViewById<TextView>(R.id.tvDinhKem)

        fun bind(item: BenhAn) {
            tvTenBenh.text = "Tên bệnh: ${item.tenBenh}"
            tvMoTa.text = "Mô tả: ${item.moTa}"

            if (item.duongDanTep?.isNotEmpty() == true) {
                tvDinhKem.text = "Tệp đính kèm: ${item.duongDanTep}"
                tvDinhKem.visibility = View.VISIBLE
            } else {
                tvDinhKem.visibility = View.GONE
            }
        }
    }
}