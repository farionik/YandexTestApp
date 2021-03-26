package com.farionik.yandextestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.RvItemCryptoBinding
import com.farionik.yandextestapp.repository.database.crypto.CryptoEntity

class CryptoAdapter : ListAdapter<CryptoEntity, CryptoAdapter.CryptoHolder>(CryptoDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemCryptoBinding.inflate(layoutInflater, parent, false)
        return CryptoHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapAdapter(data: List<CryptoEntity>) {
        submitList(data.toMutableList())
    }

    inner class CryptoHolder(
        private val binding: RvItemCryptoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CryptoEntity) {
            with(binding) {
                snippet.setBackgroundResource(
                    if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                    else R.drawable.snippet_background_light
                )

                ticker.text = item.symbol
                name.text = item.name
                item.price?.run {
                    val priceText = "${item.price} ${item.currency}"
                    price.text = priceText
                }

                val regionText = "region ${item.region}"
                region.text = regionText
            }
        }
    }

    private class CryptoDC : DiffUtil.ItemCallback<CryptoEntity>() {
        override fun areItemsTheSame(oldItem: CryptoEntity, newItem: CryptoEntity): Boolean =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: CryptoEntity, newItem: CryptoEntity): Boolean =
            oldItem == newItem

    }
}