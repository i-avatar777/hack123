package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterCatalogUIModel(
    val id: String,
    val type: FilterCatalogItemType,
    val title: String,
    val isChecked: Boolean
) : Parcelable, RecyclerViewItem {

    override fun getId(): Any = id

    companion object {

        private const val INT_DEFAULT = 0
        private const val STRING_DEFAULT = ""
        private const val BOOL_DEFAULT = false

        fun NameTechUIModel.toUIModel(type: FilterCatalogItemType, isChecked: Boolean = BOOL_DEFAULT): FilterCatalogUIModel =
            FilterCatalogUIModel(
                this.idName,
                type,
                this.nameSpec,
                isChecked
            )

        fun TypeTechUIModel.toUIModel(type: FilterCatalogItemType, isChecked: Boolean = BOOL_DEFAULT): FilterCatalogUIModel =
            FilterCatalogUIModel(
                this.idTech,
                type,
                this.techSpec,
                isChecked
            )

        fun empty(): FilterCatalogUIModel =
            FilterCatalogUIModel(
                STRING_DEFAULT,
                FilterCatalogItemType.None,
                STRING_DEFAULT,
                BOOL_DEFAULT
            )
    }
}