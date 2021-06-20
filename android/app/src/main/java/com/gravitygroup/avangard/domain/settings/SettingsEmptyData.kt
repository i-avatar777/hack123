package com.gravitygroup.avangard.domain.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsEmptyData(
    val empty: String
) : Parcelable
