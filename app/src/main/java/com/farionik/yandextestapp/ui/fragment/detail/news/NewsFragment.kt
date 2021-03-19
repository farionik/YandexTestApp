package com.farionik.yandextestapp.ui.fragment.detail.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.CompanySpaceItemDecoration

class NewsFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        initAdapter()
        companyViewModel.newsLiveData.observe(viewLifecycleOwner, { adapter.swapData(it) })
    }

    private fun initAdapter() {
        adapter = NewsAdapter(object : NewsAdapter.Interaction {
            override fun onNewsClicked(newsEntity: NewsEntity) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(newsEntity.url)
                startActivity(intent)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CompanySpaceItemDecoration())
    }
}