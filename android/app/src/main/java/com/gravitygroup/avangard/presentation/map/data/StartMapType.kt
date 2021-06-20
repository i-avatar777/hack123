package com.gravitygroup.avangard.presentation.map.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

sealed class StartMapType : Parcelable {

    @Parcelize
    object OpenOrders : StartMapType()

    @Parcelize
    data class MasterOrders(
        val currentDay: Date
    ) : StartMapType()
}