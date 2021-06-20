package com.gravitygroup.avangard.core.ext

import android.content.res.Resources

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


