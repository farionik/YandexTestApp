package com.farionik.yandextestapp.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.databinding.RvItemShippetBinding


class CompanyAdapter(private val interaction: Interaction? = null) :
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
            }
        }

        fun bind(item: CompanyEntity) = with(binding) {
            snippet.setBackgroundResource(
                if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                else R.drawable.snippet_background_light
            )

            Glide
                .with(image)
                .load(item.logo)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(52)))
                .error(R.drawable.ic_broken_image)
                .fallback(R.drawable.ic_broken_image)
                .into(image)

            favourite.apply {
                if (item.isFavourite) setImageResource(R.drawable.ic_star_gold)
                else setImageResource(R.drawable.ic_star_grey)
            }

            binding.price.text = "$${item.price}"

            item.price?.let {
                val changeText = "$$it"
                binding.change.text = changeText
                binding.change.setTextColor(getColorForTextView(it))
            }

            item.changePercent?.let {
                val percentText = " ($it)"
                binding.percentChange.text = percentText
                binding.percentChange.setTextColor(getColorForTextView(it))
            }

            ticker.text = item.symbol
            name.text = item.companyName
        }

        private fun getColorForTextView(value: Double) = run {
            if (value > 0.0) return@run ContextCompat.getColor(context, R.color.color_percent_green)
            if (value < 0.0) return@run ContextCompat.getColor(context, R.color.color_percent_red)
            ContextCompat.getColor(context, R.color.color_black)
        }
    }

    interface Interaction {
        fun likeCompany(companyEntity: CompanyEntity, position: Int)
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