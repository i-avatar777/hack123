package com.gravitygroup.avangard.view.loading

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.color
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.ViewLoadingBinding

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle),
    LoadingPlaceholder {

    private val vb: ViewLoadingBinding by viewBinding(ViewLoadingBinding::inflate)

    private var bgColorFilled = color(R.color.white)
    private var bgColorLoading = color(R.color.black2_60)
    private var bgColorTransparent = Color.TRANSPARENT
    private var textColorLight = color(R.color.white)
    private var textColorDark = color(R.color.black2)

    var text: String = ""
        set(value) {
            field = value
            vb.tvText.text = field
        }

    var textDark: Boolean = TEXT_DARK_DEFAULT
        set(value) {
            field = value
            if (value) {
                vb.tvText.setTextColor(textColorDark)
            } else {
                vb.tvText.setTextColor(textColorLight)
            }
        }

    init {
        isClickable = true
        getAttributes(context, attrs)
    }

    override fun render(state: LoadingState) {
        when (state) {
            is LoadingState.None -> isVisible = false
            is LoadingState.Loading -> showWithBackground(bgColorLoading)
            is LoadingState.Transparent -> showWithBackground(bgColorTransparent)
            is LoadingState.Filled -> showWithBackground(bgColorFilled)
        }
    }

    private fun showWithBackground(color: Int) {
        setBackgroundColor(color)
        isVisible = true
    }

    private fun getAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingView,
                0,
                0
            )
            try {
                text = array.getString(R.styleable.LoadingView_loading_text)
                    ?: context.getString(R.string.loading)
                textDark =
                    array.getBoolean(R.styleable.LoadingView_loading_text_dark, TEXT_DARK_DEFAULT)
            } finally {
                array.recycle()
            }
        }
    }

    companion object {
        const val TEXT_DARK_DEFAULT = false
    }
}
