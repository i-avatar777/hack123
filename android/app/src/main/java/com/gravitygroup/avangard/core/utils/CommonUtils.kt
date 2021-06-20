package com.gravitygroup.avangard.core.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.gravitygroup.avangard.core.ext.hideKeyboard

object CommonUtils {

    fun hideKeyboard(context: Context, view: View) {
        view.hideKeyboard()
    }

    fun getSoftBarHeight(resources: Resources): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

}
