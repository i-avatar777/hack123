package com.gravitygroup.avangard.core.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.viewbinding.ViewBinding
import com.gravitygroup.avangard.core.delegates.CustomViewBindingDelegate

fun View.color(@ColorRes color: Int): Int =
    context.color(color)

fun <T : ViewBinding> View.viewBinding(
    viewBindingFactory: (LayoutInflater, ViewGroup) -> T,
    ignoreLifecycle: Boolean = false
) = CustomViewBindingDelegate(this, viewBindingFactory, ignoreLifecycle)

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

inline fun View.waitForLayout(crossinline f: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            f()
        }
    })
}

fun View.makeVisible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.makeInvisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.makeGone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}
