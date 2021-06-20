package com.gravitygroup.avangard.core.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.color(@ColorRes color: Int): Int =
    ContextCompat.getColor(this, color)

fun Context.getNotGrantedPermissions(list: List<String>) = list.filter {
    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
}

fun Context.drawable(@DrawableRes drawable: Int): Drawable? =
    ContextCompat.getDrawable(this, drawable)

fun Context.toClipboard(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("id", text)
    clipboardManager.setPrimaryClip(clipData)
}
