package com.gravitygroup.avangard.presentation.dialogs.exit

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmDialogSpecs(
    val title: String,
    val confirm: String,
    val deny: String,
) : Parcelable