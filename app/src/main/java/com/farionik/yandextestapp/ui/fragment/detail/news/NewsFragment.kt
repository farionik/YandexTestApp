package com.farionik.yandextestapp.ui.fragment.detail.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.farionik.yandextestapp.databinding.FragmentNewsBinding
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class NewsFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private var stockModelRelation: StockModelRelation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        companyViewModel.newsLiveData.observe(viewLifecycleOwner, { newsAdapter.swapData(it) })
        companyViewModel.newsLoadingState.observe(viewLifecycleOwner, {
            it?.let {
                binding.swipeRefresh.isRefreshing = it.state == WorkInfo.State.RUNNING
            }
        })
        companyViewModel.selectedStock.observe(viewLifecycleOwner, {
            stockModelRelation = it
        })
        binding.swipeRefresh.setOnRefreshListener { companyViewModel.fetchNews(stockModelRelation) }
    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter(object : NewsAdapter.Interaction {
            override fun onNewsClicked(newsEntity: NewsEntity) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(newsEntity.url)
                startActivity(intent)
            }
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
            adapter = newsAdapter
            addItemDecoration(CompanySpaceItemDecoration())
        }
    }
}