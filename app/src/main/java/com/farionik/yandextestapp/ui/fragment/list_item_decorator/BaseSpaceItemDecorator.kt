package com.farionik.yandextestapp.ui.fragment.list_item_decorator

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

open class BaseSpaceItemDecorator : RecyclerView.ItemDecoration() {

    lateinit var context: Context

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        context = view.context
    }

    protected fun pxSpace(resource: Int): Int = context.resources.getDimensionPixelSize(resource)

    protected fun RecyclerView.isFirstPosition(view: View) = getChildAdapterPosition(view) == 0
    protected fun RecyclerView.isSecondPosition(view: View) = getChildAdapterPosition(view) == 1
    protected fun RecyclerView.isFirstOrSecondPosition(view: View) =
        isFirstPosition(view) or isSecondPosition(view)


    protected fun RecyclerView.isPreLastPosition(view: View, adapterSize: Int) =
        adapterSize - 2 == getChildAdapterPosition(view)

    protected fun RecyclerView.isLastPosition(view: View, adapterSize: Int) =
        adapterSize - 1 == getChildAdapterPosition(view)

    protected fun RecyclerView.isLastOrPreLastPosition(view: View, adapterSize: Int) =
        isLastPosition(view, adapterSize) or isPreLastPosition(view, adapterSize)
}