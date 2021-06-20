package com.gravitygroup.avangard.presentation.orders.data

import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class OrderShortUIModel(

    val id: Long,

    val dateTimeCreate: Long,

    val num: String,

    val latitude: String,

    val tel: String,

    val typeTech: String,

    val nameTech: String,

    val addressName: String,

    val longitude: String,

    val status: Int
) : RecyclerViewItem, Parcelable, Serializable {
    override fun getId() = id

    companion object {

        private const val LONG_DEFAULT = 0L
        private const val INT_DEFAULT = 0
        private const val STRING_DEFAULT = ""

        fun empty(): OrderShortUIModel =
            OrderShortUIModel(
                LONG_DEFAULT,
                LONG_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                STRING_DEFAULT,
                INT_DEFAULT
            )

        fun OrderFullUIModel.toShort(typeTech: String, nameTech: String): OrderShortUIModel =
            OrderShortUIModel(
                this.id,
                this.dateTimeCreate,
                this.num,
                this.latitude,
                this.tel,
                typeTech,
                nameTech,
                this.addressName,
                this.longitude,
                this.status
            )

        fun HistoryShortOrder.toOrigin(): OrderShortUIModel =
            OrderShortUIModel(
                this.id,
                this.dateTimeCreate,
                this.num,
                this.latitude,
                this.tel,
                this.typeTech,
                this.nameTech,
                this.addressName,
                this.longitude,
                this.status
            )
    }
}