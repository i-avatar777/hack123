package com.gravitygroup.avangard.presentation.yacht

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class YachtListSpecs(
    val placeCount: Int,
    val typeRent: Int,
    val priceFrom: Int,
    val priceTo: Int
): Parcelable