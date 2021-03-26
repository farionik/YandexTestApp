package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentStocksBinding
import com.farionik.yandextestapp.ui.adapter.CryptoAdapter
import com.farionik.yandextestapp.ui.adapter.PaginationListener
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.CryptoViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CryptoFragment : Fragment(R.layout.fragment_stocks) {

    private lateinit var binding: FragmentStocksBinding
    private val cryptoViewModel by sharedViewModel<CryptoViewModel>()
    private lateinit var cryptoAdapter: CryptoAdapter
    private var workState: WorkInfo.State? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStocksBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(cryptoViewModel)

        initAdapter()
        cryptoViewModel.downloadCryptoState.observe(viewLifecycleOwner, {
            it?.let {
                workState = it.state
            }
        })
        cryptoViewModel.cryptoLiveData.observe(viewLifecycleOwner, {cryptoAdapter.swapAdapter(it)})
    }

    private fun initAdapter() {
        cryptoAdapter = CryptoAdapter();
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = cryptoAdapter
        }
    }
}