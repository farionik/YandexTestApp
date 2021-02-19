package com.farionik.yandextestapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val appBarOffsetMutableLiveData = MutableLiveData<Int>()
}