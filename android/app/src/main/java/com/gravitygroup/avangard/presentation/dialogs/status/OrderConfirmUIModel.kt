package com.gravitygroup.avangard.presentation.dialogs.status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderConfirmUIModel(
    val orderId: Long,
    val confirmType: OrderStatusConfirmType
) : Parcelable