package com.gravitygroup.avangard.presentation.orders.data

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel

data class OrderFilterUIModel(
    val index: Int,
    val type: FilterTypeUIModel = FilterTypeUIModel.None
) : RecyclerViewItem {

    override fun getId(): Any = index

    companion object {

        private const val INDEX_DEFAULT = -1

        fun empty(): OrderFilterUIModel = OrderFilterUIModel(INDEX_DEFAULT)
    }
}