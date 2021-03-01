package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.databinding.RvItemShippetBinding
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue

class CompanyAdapter(
    private val isSearch: Boolean = false,
    private val interaction: Interaction? = null
) :
    ListAdapter<CompanyEntity, CompanyAdapter.CompanyHolder>(CompanyEntityDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemShippetBinding.inflate(layoutInflater, parent, false)
        return CompanyHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: CompanyHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<CompanyEntity>) {
        submitList(data.toMutableList())
    }

    inner class CompanyHolder(
        private val binding: RvItemShippetBinding,
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
                return
            }
            interaction?.openCompanyDetail(clicked)
        }

        fun bind(item: CompanyEntity) = with(binding) {
            snippet.setBackgroundResource(
                when (isSearch) {
                    true -> {
                        if (item.isFavourite) R.drawable.snippet_background_light
                        else R.drawable.snippet_background_dark
                    }
                    false -> {
                        if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                        else R.drawable.snippet_background_light
                    }
                }
            )

            Glide
                .with(image)
                .load(item.logo)
                .priority(Priority.HIGH)
                .transform(CenterCrop(), RoundedCorners(52))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.ic_broken_image)
                .fallback(R.drawable.ic_broken_image)
                .into(image)

            favourite.run {
                if (item.isFavourite) setImageResource(R.drawable.ic_star_gold)
                else setImageResource(R.drawable.ic_star_grey)
            }

            binding.price.formatPriceValue(item)
            binding.change.formatChangeValue(item)
            binding.percentChange.formatPercentValue(item)

            ticker.text = item.symbol
            name.text = item.companyName
        }
    }

    interface Interaction {
        fun likeCompany(companyEntity: CompanyEntity, position: Int)

        fun openCompanyDetail(companyEntity: CompanyEntity)
    }

    private class CompanyEntityDC : DiffUtil.ItemCallback<CompanyEntity>() {
        override fun areItemsTheSame(
            oldItem: CompanyEntity,
            newItem: CompanyEntity
        ): Boolean = oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(
            oldItem: CompanyEntity,
            newItem: CompanyEntity
        ): Boolean = oldItem == newItem
    }
}