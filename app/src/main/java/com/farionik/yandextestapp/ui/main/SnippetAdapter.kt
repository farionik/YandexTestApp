package com.farionik.yandextestapp.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.data.StockEntity
import com.farionik.yandextestapp.databinding.RvItemShippetBinding

class SnippetAdapter(private val interaction: Interaction? = null) :
    ListAdapter<StockEntity, SnippetAdapter.SnippetHolder>(SnippetEntityDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnippetHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemShippetBinding.inflate(layoutInflater, parent, false)
        return SnippetHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: SnippetHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<StockEntity>) {
        submitList(data.toMutableList())
    }

    inner class SnippetHolder(
        private val binding: RvItemShippetBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
        }

        fun bind(item: StockEntity) = with(binding) {
            snippet.setBackgroundResource(
                if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                else R.drawable.snippet_background_light
            )
            image.load(R.mipmap.ic_launcher) {
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(52f))
            }
            favourite.load(
                if (adapterPosition % 2 == 0) R.drawable.ic_star_gold
                else R.drawable.ic_star_grey
            )

            ticker.text = item.Symbol
            name.text = item.Name
            price.text = item.price
            percent.text = item.percent
        }
    }

    interface Interaction {

    }

    private class SnippetEntityDC : DiffUtil.ItemCallback<StockEntity>() {
        override fun areItemsTheSame(
            oldItem: StockEntity,
            newItem: StockEntity
        ): Boolean = oldItem.Symbol == newItem.Symbol

        override fun areContentsTheSame(
            oldItem: StockEntity,
            newItem: StockEntity
        ): Boolean = oldItem == newItem
    }
}