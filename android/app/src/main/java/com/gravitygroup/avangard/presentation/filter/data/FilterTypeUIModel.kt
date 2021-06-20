package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import androidx.annotation.StringRes
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType.NAME
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType.TYPE
import kotlinx.android.parcel.Parcelize

sealed class FilterTypeUIModel(
    @StringRes val titleRes: Int
) : Parcelable {

    @Parcelize
    object Date : FilterTypeUIModel(R.string.filter_type_date)

    @Parcelize
    data class Type(
        val title: String = STRING_DEFAULT
    ) : FilterTypeUIModel(R.string.filter_type_customer_tech_type)

    @Parcelize
    data class Name(
        val title: String = STRING_DEFAULT
    ) : FilterTypeUIModel(R.string.filter_type_customer_tech_name)

    @Parcelize
    object Number : FilterTypeUIModel(R.string.filter_type_order_num)

    @Parcelize
    object CustomerPhone : FilterTypeUIModel(R.string.filter_type_customer_phone)

    @Parcelize
    object None : FilterTypeUIModel(R.string.filter_type_none)

    companion object {

        private const val STRING_DEFAULT = ""

        fun CatalogItemType.toFilter(): FilterTypeUIModel =
            when (this) {
                TYPE -> Type()
                NAME -> Name()
                else -> None
            }
    }
}