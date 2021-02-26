package com.farionik.yandextestapp.ui.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.fragment.BaseFragment

class CompanyDetailFragment : BaseFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_detail, container, false)
    }
}