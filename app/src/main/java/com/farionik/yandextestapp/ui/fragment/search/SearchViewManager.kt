package com.farionik.yandextestapp.ui.fragment.search

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
import com.farionik.yandextestapp.ui.AppScreens
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.github.terrakok.cicerone.Router
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class SearchViewManager(
    private val editText: EditText,
    private val router: Router,
    private val searchViewModel: SearchViewModel
) {

    init {
        editText.initSearchEditText()
    }

    enum class SearchState {
        ACTIVE, NOT_ACTIVE, SEARCH
    }

    private var state = SearchState.NOT_ACTIVE

    private fun EditText.initSearchEditText() {
        apply {
            initTextChangeListener()
            initFilter()
            initTouchListener()
        }
    }

    private fun EditText.initTextChangeListener() {
        setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchAction()
                    return true
                }
                return false
            }
        })

        onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            run {
                hint = if (hasFocus) {
                    changeEditTextState(SearchState.ACTIVE)
                    router.navigateTo(AppScreens.searchScreen(), false)
                    ""
                } else {
                    changeEditTextState(SearchState.NOT_ACTIVE)
                    context.getString(R.string.find_company_or_ticker)
                }
            }
        }
    }

    fun searchAction(){
        if (state != SearchState.SEARCH) {
            router.navigateTo(AppScreens.searchResultScreen(), false)
            editText.changeEditTextState(SearchState.SEARCH)
        }
        KeyboardUtils.hideSoftInput(editText)
        searchViewModel.searchCompanies(editText.text.toString())
    }


    private fun EditText.initFilter() {
        // фильтровать ввод пробела как первого символа
        val filter = InputFilter { source, start, _, _, _, _ ->
            if ((start == 0) and source.toString().isBlank()) {
                return@InputFilter ""
            }
            return@InputFilter source
        }
        filters = arrayOf(filter)
    }

    private fun EditText.changeEditTextState(s: SearchState) {
        state = s
        fun getDrawable(resourceId: Int) = ContextCompat.getDrawable(context, resourceId)

        val backDrawable = getDrawable(R.drawable.ic_back)
        val closeDrawable = getDrawable(R.drawable.ic_close)
        val searchDrawable = getDrawable(R.drawable.ic_search)

        when (state) {
            SearchState.ACTIVE -> {
                setCompoundDrawablesWithIntrinsicBounds(
                    backDrawable,
                    null,
                    null,
                    null
                )
            }
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

                    width?.let {
                        if (x >= (right - it - padding)) {
                            setText("")
                            router.exit()
                            changeEditTextState(SearchState.ACTIVE)
                            return true
                        }
                    }

                    // проверка нажатия на левую картинку
                    width = compoundDrawables[DRAWABLE_LEFT].bounds.width()
                    if (x <= (width + padding)) {
                        when (state) {
                            SearchState.NOT_ACTIVE -> requestFocus()
                            else -> handleBackClicked()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun handleBackClicked() {
        when (state) {
            SearchState.SEARCH -> {
                editText.changeEditTextState(SearchState.ACTIVE)
                router.exit()
            }
            SearchState.ACTIVE -> {
                editText.setText("")
                router.exit()
                editText.clearFocus()
                editText.let { KeyboardUtils.hideSoftInput(it) }
            }
            else -> {
            }
        }
    }

    fun systemBackClicked(): Boolean = when (state) {
        SearchState.NOT_ACTIVE -> false
        else -> {
            handleBackClicked()
            true
        }
    }

}