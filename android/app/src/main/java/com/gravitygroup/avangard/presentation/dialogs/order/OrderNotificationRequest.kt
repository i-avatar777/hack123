package com.gravitygroup.avangard.presentation.dialogs.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderNotificationRequest(
    val notificationTimeorder: String,
    val comment: String?,
) : Parcelable
