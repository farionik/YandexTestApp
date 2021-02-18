package com.farionik.yandextestapp.ui.search

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

class SearchSpaceItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.adapter?.let {

            val isFirstPosition = parent.getChildAdapterPosition(view) == 0
            val isSecondPosition = parent.getChildAdapterPosition(view) == 1

            val preLastPosition = it.itemCount - 2 == parent.getChildAdapterPosition(view)
            val lastPosition = it.itemCount - 1 == parent.getChildAdapterPosition(view)

            val context = view.context
            val spacingInPixels: Int = if (isFirstPosition or isSecondPosition) {
                context.resources.getDimensionPixelSize(R.dimen._16sdp)
            } else {
                context.resources.getDimensionPixelSize(R.dimen._4sdp)
            }
            outRect.left = spacingInPixels

            if (preLastPosition or lastPosition) {
                outRect.right = context.resources.getDimensionPixelSize(R.dimen._16sdp)
            }
        }

    }
}