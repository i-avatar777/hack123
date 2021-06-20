package com.gravitygroup.avangard.core.ext

import android.os.CountDownTimer
import androidx.fragment.app.Fragment

private const val COUNT_DOWN_INTERVAL = 1000L

fun Fragment.initDownTimer(
    millisInFuture: Long,
    tickHandler: (Long) -> Unit,
    finishHandler: () -> Unit
): CountDownTimer {
    val cdt = object: CountDownTimer(millisInFuture, COUNT_DOWN_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            if (isAdded) tickHandler(millisUntilFinished)
        }

        override fun onFinish() {
            if (isAdded) finishHandler()
        }
    }
    cdt.start()
    return cdt
}