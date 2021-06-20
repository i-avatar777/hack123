package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FilterCatalogItemType : Parcelable {

    @Parcelize
    object Type : FilterCatalogItemType()

    @Parcelize
    object Name : FilterCatalogItemType()

    @Parcelize
    object None : FilterCatalogItemType()
}