package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterCatalogSpecs(
    val type: FilterCatalogItemType,
    val checkedItems: List<String>
) : Parcelable