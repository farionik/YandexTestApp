package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.RvItemCompanyBinding
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue

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