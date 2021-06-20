package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.domain.orders.EventStatus.TIME
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventData(
    val id: Long,
    val title: String,
    val hour: Int,
    val minute: Int,
    val duration: Int,
    val status: EventStatus,

): RecyclerViewItem, Parcelable {
    override fun getId() = id

    companion object {

        private const val INT_DEFAULT = 0

        fun OrderFullUIModel.toEvent(title: String): EventData =
            EventData(
                this.id,
                title,
                INT_DEFAULT,
                INT_DEFAULT,
                INT_DEFAULT,
                TIME
            )

        fun OrderShortUIModel.toEvent(title: String): EventData =
            EventData(
                this.id,
                title,
                INT_DEFAULT,
                INT_DEFAULT,
                INT_DEFAULT,
                TIME
            )
    }
}