package com.gravitygroup.avangard.presentation.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SmsCodeSpecs(
    val login: String,
    val password: String
) : Parcelable