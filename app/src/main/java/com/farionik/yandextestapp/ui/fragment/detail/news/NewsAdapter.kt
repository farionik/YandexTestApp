package com.farionik.yandextestapp.ui.fragment.detail.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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
import com.farionik.yandextestapp.databinding.RvItemNewsBinding
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val interaction: NewsAdapter.Interaction? = null) :
    ListAdapter<NewsEntity, NewsAdapter.NewsHolder>(NewsEntityDC()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = RvItemNewsBinding.inflate(layoutInflater, parent, false)
        return NewsHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<NewsEntity>) {
        submitList(data.toMutableList())
    }

    inner class NewsHolder(
        private val binding: RvItemNewsBinding,
        private val interaction: NewsAdapter.Interaction?
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            val clicked = getItem(adapterPosition)
            interaction?.onNewsClicked(clicked)
        }

        fun bind(item: NewsEntity) = with(binding) {

            snippet.setBackgroundResource(
                if (adapterPosition % 2 == 0) R.drawable.snippet_background_dark
                else R.drawable.snippet_background_light
            )

            Glide
                .with(image)
                .load(item.image)
                .priority(Priority.HIGH)
                .transform(CenterCrop(), RoundedCorners(52))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(image)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm aa", Locale.US)
            val date = Date(item.datetime)
            tvDate.text = simpleDateFormat.format(date)
            tvSource.text = item.source
            tvHeadLine.text = item.headline
            tvSummary.text = item.summary
        }
    }

    interface Interaction {
        fun onNewsClicked(newsEntity: NewsEntity)
    }

    private class NewsEntityDC : DiffUtil.ItemCallback<NewsEntity>() {
        override fun areItemsTheSame(
            oldItem: NewsEntity,
            newItem: NewsEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NewsEntity,
            newItem: NewsEntity
        ): Boolean = oldItem == newItem
    }
}