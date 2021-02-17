package com.farionik.yandextestapp.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.RvItemShippetBinding

class SnippetAdapter(private val interaction: Interaction? = null) :
    ListAdapter<SnippetEntity, SnippetAdapter.SnippetHolder>(SnippetEntityDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnippetHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemShippetBinding.inflate(layoutInflater, parent, false)
        return SnippetHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: SnippetHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<SnippetEntity>) {
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

        fun bind(item: SnippetEntity) = with(binding) {
            snippet.setBackgroundResource(
                if (adapterPosition % 2 == 0) {
                    R.drawable.snippet_background_dark
                } else{
                    R.drawable.snippet_background_light
                }
            )

            ticker.text = item.ticker
        }
    }

    interface Interaction {

    }

    private class SnippetEntityDC : DiffUtil.ItemCallback<SnippetEntity>() {
        override fun areItemsTheSame(
                oldItem: SnippetEntity,
                newItem: SnippetEntity
        ): Boolean {
            TODO(
                "not implemented"
            )
        }

        override fun areContentsTheSame(
                oldItem: SnippetEntity,
                newItem: SnippetEntity
        ): Boolean {
            TODO(
                "not implemented"
            )
        }
    }
}