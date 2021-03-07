package com.farionik.yandextestapp.ui.util

import java.text.SimpleDateFormat
import java.util.*


val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

fun getFormattedCurrentDate(): String = sdf.format(Date())


