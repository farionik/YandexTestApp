package com.farionik.yandextestapp.ui.main

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

class SpaceItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.adapter?.let {

            val isFirstPosition = parent.getChildAdapterPosition(view) == 0
            val lastPosition = it.itemCount - 1 == parent.getChildAdapterPosition(view)

            val context = view.context
            val spacingInPixels: Int = if (isFirstPosition) {
                context.resources.getDimensionPixelSize(R.dimen._20sdp)
            } else {
                context.resources.getDimensionPixelSize(R.dimen._8sdp)
            }
            outRect.top = spacingInPixels


            if (lastPosition) {
                outRect.bottom = context.resources.getDimensionPixelSize(R.dimen._20sdp)
            }
        }

    }
}