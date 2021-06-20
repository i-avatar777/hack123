package com.gravitygroup.avangard.presentation.dialogs.status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class OrderStatusConfirmType : Parcelable {
    Accept,
    AcceptReject,
    Complete,
}