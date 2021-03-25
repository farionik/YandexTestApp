package com.farionik.yandextestapp.ui.fragment.search

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

class SearchAdapter(private val interaction: Interaction? = null) :
    ListAdapter<ISearchModel, SearchAdapter.SearchHolder>(SearchModelDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_search, parent, false), interaction
    )

    override fun onBindViewHolder(holder: SearchHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<ISearchModel>) {
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

        fun bind(item: ISearchModel) = with(itemView) {
            findViewById<TextView>(R.id.title).text = item.title()
        }
    }

    interface Interaction {
        fun onModelClicked(model: ISearchModel)
    }

    private class SearchModelDC : DiffUtil.ItemCallback<ISearchModel>() {
        override fun areItemsTheSame(
            oldItem: ISearchModel,
            newItem: ISearchModel
        ): Boolean {
            return oldItem.id() == newItem.id()
        }

        override fun areContentsTheSame(
            oldItem: ISearchModel,
            newItem: ISearchModel
        ): Boolean {
            return oldItem.content() == newItem.content()
        }
    }
}