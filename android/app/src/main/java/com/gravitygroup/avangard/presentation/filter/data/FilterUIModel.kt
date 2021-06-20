package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import com.gravitygroup.avangard.presentation.catalog.data.CatalogUIModel
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel.None
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterUIModel(
    val type: FilterTypeUIModel,
    val item: CatalogUIModel,
    val numberOrder: String = STRING_DEFAULT,
    val phoneOrder: String = STRING_DEFAULT
) : Parcelable {

    companion object {

        private const val STRING_DEFAULT = ""

        fun empty(): FilterUIModel =
            FilterUIModel(
                None,
                CatalogUIModel.empty(),
                STRING_DEFAULT,
                STRING_DEFAULT
            )
    }
}