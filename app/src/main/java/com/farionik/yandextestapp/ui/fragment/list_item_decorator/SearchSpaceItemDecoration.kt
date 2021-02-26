package com.farionik.yandextestapp.ui.fragment.list_item_decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

class SearchSpaceItemDecoration : BaseSpaceItemDecorator() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.run {
            adapter?.run {
                outRect.left = if (isFirstOrSecondPosition(view)) {
                    pxSpace(R.dimen._16sdp)
                } else {
                    pxSpace(R.dimen._4sdp)
                }

                if (parent.isLastOrPreLastPosition(view, itemCount)) {
                    outRect.right = pxSpace(R.dimen._16sdp)
                }
            }
        }
    }
}