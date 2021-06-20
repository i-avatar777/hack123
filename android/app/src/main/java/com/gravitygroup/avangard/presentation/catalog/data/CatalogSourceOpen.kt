package com.gravitygroup.avangard.presentation.catalog.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class CatalogSourceOpen : Parcelable {

    @Parcelize
    object Edit : CatalogSourceOpen()

    @Parcelize
    object Filter : CatalogSourceOpen()
}