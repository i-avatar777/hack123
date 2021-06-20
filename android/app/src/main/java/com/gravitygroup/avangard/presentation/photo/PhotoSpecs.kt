package com.gravitygroup.avangard.presentation.photo

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoSpecs(
    val title: String,
    val pathEncoded: String = "",
    val uri: Uri = Uri.EMPTY
): Parcelable