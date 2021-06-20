package com.gravitygroup.avangard.core.ext

import java.util.*

fun Calendar.clearTimeFields() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}