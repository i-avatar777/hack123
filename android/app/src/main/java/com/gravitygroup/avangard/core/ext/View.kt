package com.gravitygroup.avangard.core.ext

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.utils.CommonUtils

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrHide(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun EditText.hideKeyboardOnOutClick(context: Context) {
    setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) CommonUtils.hideKeyboard(context, v)
    }
}

fun Window.setEdgeToEdge() {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setDecorFitsSystemWindows(false)
    } else {
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    statusBarColor = context.color(R.color.statusBarColor)
    navigationBarColor = context.color(R.color.navBarColor)
}

fun View.doOnApplyWindowInsets(listener: (View, WindowInsetsCompat) -> WindowInsetsCompat) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        listener(view, insets)
    }
    requestApplyInsetsWhenAttached()
}

fun View.clearInsetsListener() {
    ViewCompat.setOnApplyWindowInsetsListener(this, null)
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        doOnAttach {
            requestApplyInsets()
        }
    }
}

fun View.fadeIn(duration: Long) {
    animate().alpha(1f)
        .setDuration(duration)
        .start()
}