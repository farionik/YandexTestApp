package com.farionik.yandextestapp.ui.adapter

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

class StockHolder(
    private val binding: RvItemCompanyBinding,
    private val interaction: Interaction?,
    private val isSearch: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: StockModelRelation?) = with(binding) {

        item?.run {
            root.setOnClickListener { interaction?.openCompanyDetail(this) }

            Glide.with(image).clear(image)
            logo?.run {
                Glide
                    .with(image)
                    .load(localPath)
                    .transform(CenterCrop(), RoundedCorners(52))
                    .into(image)
            }

            stock.run {
                ticker.text = symbol
                name.text = companyName

                snippet.setBackgroundResource(
                    when (isSearch) {
                        true -> {
                            if (isFavourite) R.drawable.snippet_background_light
                            else R.drawable.snippet_background_dark
                        }
                        false -> {
                            if (absoluteAdapterPosition % 2 == 0) R.drawable.snippet_background_dark
                            else R.drawable.snippet_background_light
                        }
                    }
                )
                favourite.setImageResource(
                    if (isFavourite) R.drawable.ic_star_gold
                    else R.drawable.ic_star_grey
                ).also {
                    favourite.setOnClickListener {
                        interaction?.likeCompany(
                            item,
                            absoluteAdapterPosition
                        )
                    }
                }


                binding.price.formatPriceValue(this)
                binding.change.formatChangeValue(this)
                binding.percentChange.formatPercentValue(this)
            }
        }
    }
}

interface Interaction {
    fun likeCompany(stockModelRelation: StockModelRelation, position: Int)

    fun openCompanyDetail(stockModelRelation: StockModelRelation)
}

class StockDC : DiffUtil.ItemCallback<StockModelRelation>() {
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