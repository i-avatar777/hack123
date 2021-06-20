package com.gravitygroup.avangard.domain.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthData(
    val token: String,
    val login: String,
    val fio: String
) : Parcelable
