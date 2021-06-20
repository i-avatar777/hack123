package com.gravitygroup.avangard.presentation.orders_history.data

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryItemType.Header
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryItemType.Info

data class HistoryShortOrder(
    val id: Long,

    val dateTimeCreate: Long,

    val num: String,

    val latitude: String,

    val tel: String,

    val typeTech: String,

    val nameTech: String,

    val addressName: String,

    val longitude: String,

    val status: Int,

    val itemType: HistoryItemType
) : RecyclerViewItem {

    override fun getId(): Any = id

    companion object {

        fun OrderShortUIModel.toHistory(): HistoryShortOrder =
            HistoryShortOrder(
                this.id,
                this.dateTimeCreate,
                this.num,
                this.latitude,
                this.tel,
                this.typeTech,
                this.nameTech,
                this.addressName,
                this.longitude,
                this.status,
                Info
            )

        fun OrderShortUIModel.toHeader(): HistoryShortOrder =
            HistoryShortOrder(
                this.id,
                this.dateTimeCreate,
                this.num,
                this.latitude,
                this.tel,
                this.typeTech,
                this.nameTech,
                this.addressName,
                this.longitude,
                this.status,
                Header
            )
    }
}