package com.gravitygroup.avangard.interactors.yacht.data

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.domain.yacht.ListYachtResItem

data class YachtUIModel(

    val id: Int,

    val image: String,

    val price: Int,

    val typeRent: Int,

    val placeCount: Int,

    val rating: Int
) : RecyclerViewItem {

    companion object {

        private const val STRING_DEFAULT = ""
        private const val INT_DEFAULT = 0

        fun ListYachtResItem.toUiModel(): YachtUIModel =
            YachtUIModel(
                this.id ?: INT_DEFAULT,
                this.image ?: STRING_DEFAULT,
                this.price ?: INT_DEFAULT,
                this.typeRent ?: INT_DEFAULT,
                this.placeCount ?: INT_DEFAULT,
                this.rating ?: INT_DEFAULT
            )
    }

    override fun getId(): Any {
        return id
    }
}