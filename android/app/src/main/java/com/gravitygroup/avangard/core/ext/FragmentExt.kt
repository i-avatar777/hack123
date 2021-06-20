package com.gravitygroup.avangard.core.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.gravitygroup.avangard.core.delegates.FragmentViewBindingDelegate

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

fun Fragment.hideKeyboard() {
    view?.hideKeyboard()
}

fun Activity.hideSoftKeyboard() {
    val view: View = currentFocus ?: return
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}