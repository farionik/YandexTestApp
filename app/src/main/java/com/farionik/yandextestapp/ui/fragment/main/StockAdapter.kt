package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.farionik.yandextestapp.databinding.RvItemCompanyBinding
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class StockAdapter(
    private val isSearch: Boolean = false,
    private val interaction: Interaction? = null
) :
    ListAdapter<StockModelRelation, StockHolder>(StockDC()) {

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


    fun swapData(data: List<StockModelRelation>) {
        submitList(data.toMutableList())
    }
}