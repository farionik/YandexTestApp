package com.farionik.yandextestapp.ui.fragment.list_item_decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R

class CompanySpaceItemDecoration : BaseSpaceItemDecorator() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.run {
            adapter?.run {
                outRect.top = if (isFirstPosition(view)) {
                    pxSpace(R.dimen._20sdp)
                } else {
                    pxSpace(R.dimen._8sdp)
                }

                if (parent.isLastPosition(view, itemCount)) {
                    outRect.bottom = pxSpace(R.dimen._20sdp)
                }
            }
        }
    }
}