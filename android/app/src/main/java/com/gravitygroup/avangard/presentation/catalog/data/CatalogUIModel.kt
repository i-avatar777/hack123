package com.gravitygroup.avangard.presentation.catalog.data

import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatalogUIModel(
    val id: String,
    val title: String
) : Parcelable, RecyclerViewItem {

    override fun getId(): Any = id

    companion object {

        private const val INT_DEFAULT = 0
        private const val STRING_DEFAULT = ""

        fun NameTechUIModel.toUIModel(): CatalogUIModel =
            CatalogUIModel(
                this.idName,
                this.nameSpec
            )

        fun TypeTechUIModel.toUIModel(): CatalogUIModel =
            CatalogUIModel(
                this.idTech,
                this.techSpec
            )

        fun empty(): CatalogUIModel =
            CatalogUIModel(
                STRING_DEFAULT,
                STRING_DEFAULT
            )
    }
}