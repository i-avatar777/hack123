package com.gravitygroup.avangard.domain.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginData(
    val login: String,
    val password: String,
    val code_sms: String
) : Parcelable