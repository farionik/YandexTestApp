package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.RvItemCompanyBinding
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue

class StockAdapter(
    private val isSearch: Boolean = false,
    private val interaction: Interaction? = null
) :
    ListAdapter<StockModelRelation, StockAdapter.StockHolder>(StockDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemCompanyBinding.inflate(layoutInflater, parent, false)
        return StockHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: StockHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<StockModelRelation>) {
        submitList(data.toMutableList())
    }

    inner class StockHolder(
        private val binding: RvItemCompanyBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        init {
            itemView.setOnClickListener(this)
            binding.favourite.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            val clicked = getItem(adapterPosition)
            if (v?.id == R.id.favourite) {
                interaction?.likeCompany(clicked, adapterPosition)
            } else {
                interaction?.openCompanyDetail(clicked)
            }
        }

        fun bind(item: StockModelRelation) = with(binding) {
            item.logo?.run {
                Glide
                    .with(image)
                    .load(url)
                    .priority(Priority.HIGH)
                    .transform(CenterCrop(), RoundedCorners(52))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image)
            }

            item.stock.run {
                ticker.text = symbol
                name.text = companyName

                snippet.setBackgroundResource(
                    when (isSearch) {
                        true -> {
                            if (isFavourite) R.drawable.snippet_background_light
                            else R.drawable.snippet_background_dark
                        }
                        false -> {
                            if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                            else R.drawable.snippet_background_light
                        }
                    }
                )
                favourite.setImageResource(
                    if (isFavourite) R.drawable.ic_star_gold
                    else R.drawable.ic_star_grey
                )
                binding.price.formatPriceValue(this)
                binding.change.formatChangeValue(this)
                binding.percentChange.formatPercentValue(this)
            }
        }
    }

    interface Interaction {
        fun likeCompany(stockModelRelation: StockModelRelation, position: Int)

        fun openCompanyDetail(stockModelRelation: StockModelRelation)
    }

    private class StockDC : DiffUtil.ItemCallback<StockModelRelation>() {
        override fun areItemsTheSame(
            oldItem: StockModelRelation,
            newItem: StockModelRelation
        ): Boolean = oldItem.stock.symbol == newItem.stock.symbol

        override fun areContentsTheSame(
            oldItem: StockModelRelation,
            newItem: StockModelRelation
        ): Boolean {
            return oldItem.stock == newItem.stock
        }
    }
}