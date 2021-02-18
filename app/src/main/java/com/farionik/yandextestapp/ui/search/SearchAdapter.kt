package com.farionik.yandextestapp.ui.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.model.SearchModel

class SearchAdapter(private val interaction: Interaction? = null) :
    ListAdapter<SearchModel, SearchAdapter.SearchHolder>(SearchModelDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_search, parent, false), interaction
    )

    override fun onBindViewHolder(holder: SearchHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<SearchModel>) {
        submitList(data.toMutableList())
    }

    inner class SearchHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
            interaction?.onModelClicked(clicked)
        }

        fun bind(item: SearchModel) = with(itemView) {
            findViewById<TextView>(R.id.title).text = item.name
        }
    }

    interface Interaction {
        fun onModelClicked(model: SearchModel)
    }

    private class SearchModelDC : DiffUtil.ItemCallback<SearchModel>() {
        override fun areItemsTheSame(
            oldItem: SearchModel,
            newItem: SearchModel
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: SearchModel,
            newItem: SearchModel
        ): Boolean {
            return false
        }
    }
}