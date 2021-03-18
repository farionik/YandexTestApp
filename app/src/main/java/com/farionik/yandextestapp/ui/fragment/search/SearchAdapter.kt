package com.farionik.yandextestapp.ui.fragment.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.model.SearchModel

class SearchAdapter(private val interaction: Interaction? = null) :
    ListAdapter<CompanyEntity, SearchAdapter.SearchHolder>(SearchModelDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_search, parent, false), interaction
    )

    override fun onBindViewHolder(holder: SearchHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<CompanyEntity>) {
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

        fun bind(item: CompanyEntity) = with(itemView) {
            findViewById<TextView>(R.id.title).text = item.companyName
        }
    }

    interface Interaction {
        fun onModelClicked(model: CompanyEntity)
    }

    private class SearchModelDC : DiffUtil.ItemCallback<CompanyEntity>() {
        override fun areItemsTheSame(
            oldItem: CompanyEntity,
            newItem: CompanyEntity
        ): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(
            oldItem: CompanyEntity,
            newItem: CompanyEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}