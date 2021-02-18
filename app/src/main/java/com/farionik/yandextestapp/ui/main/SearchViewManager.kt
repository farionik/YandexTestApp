package com.farionik.yandextestapp.ui.main

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R


enum class SearchState {
    ACTIVE, NOT_ACTIVE, SEARCH
}

fun EditText.initSearchEditText(callback: (SearchState) -> Unit) {
    apply {
        initTextChangeListener(callback)
        initFilter()
        initTouchListener()
    }
}

private fun EditText.initTextChangeListener(callback: (SearchState) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.isNullOrEmpty()) {
                changeEditTextState(SearchState.ACTIVE)
                callback(SearchState.ACTIVE)
            }
        }
    })


    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeEditTextState(SearchState.SEARCH)
                callback(SearchState.SEARCH)
                return true
            }
            return false
        }

    })

    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        run {
            hint = if (hasFocus) {
                changeEditTextState(SearchState.ACTIVE)
                callback(SearchState.ACTIVE)
                ""
            } else {
                changeEditTextState(SearchState.NOT_ACTIVE)
                callback(SearchState.NOT_ACTIVE)
                context.getString(R.string.find_company_or_ticker)
            }
        }
    }
}

private fun EditText.initFilter() {
    // фильтровать ввод пробела как первого символа
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        if ((start == 0) and source.toString().isBlank()) {
            return@InputFilter ""
        }
        return@InputFilter source
    }
    filters = arrayOf(filter)
}

private fun EditText.changeEditTextState(state: SearchState) {
    fun getDrawable(resourseId: Int) = ContextCompat.getDrawable(context, resourseId)

    val backDrawable = getDrawable(R.drawable.ic_back)
    val closeDrawable = getDrawable(R.drawable.ic_close)
    val searchDrawable = getDrawable(R.drawable.ic_search)

    when (state) {
        SearchState.ACTIVE -> setCompoundDrawablesWithIntrinsicBounds(
            backDrawable,
            null,
            null,
            null
        )
        SearchState.NOT_ACTIVE -> setCompoundDrawablesWithIntrinsicBounds(
            searchDrawable,
            null,
            null,
            null
        )
        SearchState.SEARCH -> setCompoundDrawablesWithIntrinsicBounds(
            backDrawable,
            null,
            closeDrawable,
            null
        )
    }

}

private fun EditText.initTouchListener() {
    setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2

            if (event?.action == MotionEvent.ACTION_DOWN) {
                val x = event.x

                // проверка нажатия на правую картинку
                var width = compoundDrawables[DRAWABLE_RIGHT]?.bounds?.width()
                val padding = resources.getDimensionPixelSize(R.dimen._16sdp)

                if (width != null) {
                    if (x >= (right - width - padding)) {
                        setText("")
                        return true
                    }
                }

                // проверка нажатия на левую картинку
                width = compoundDrawables[DRAWABLE_LEFT].bounds.width()
                if (x <= (width + padding)) {

                    setText("")
                    if (hasFocus()) {
                        clearFocus()
                        v?.let { KeyboardUtils.hideSoftInput(it) }
                    } else {
                        requestFocus()
                    }


                    return true
                }
            }
            return false
        }
    })
}