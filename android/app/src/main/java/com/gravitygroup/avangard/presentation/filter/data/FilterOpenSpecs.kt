package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FilterOpenSpecs : Parcelable {

    @Parcelize
    object Master: FilterOpenSpecs()

    @Parcelize
    object Other: FilterOpenSpecs()
}