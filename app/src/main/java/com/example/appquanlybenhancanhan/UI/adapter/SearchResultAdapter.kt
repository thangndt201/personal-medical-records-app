package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.model.SearchResult

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var results = listOf<SearchResult>()

    fun setResults(newResults: List<SearchResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount() = results.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)

        fun bind(result: SearchResult) {
            tvTitle.text = result.title
            tvDescription.text = result.description
        }
    }
}