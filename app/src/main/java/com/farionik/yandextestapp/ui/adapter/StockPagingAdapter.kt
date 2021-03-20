package com.farionik.yandextestapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.farionik.yandextestapp.databinding.RvItemCompanyBinding
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class StockPagingAdapter(
    private val interaction: Interaction? = null
) : PagingDataAdapter<StockModelRelation, StockHolder>(StockDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemCompanyBinding.inflate(layoutInflater, parent, false)
        return StockHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: StockHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateItem(position: Int) {
        val updatedStock = getItem(position)?.stock
        updatedStock?.run {
            isFavourite = !isFavourite
            notifyItemChanged(position)
        }
    }
}