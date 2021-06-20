package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.domain.orders.EditDateTimeData.Companion.toData
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditOrderData(

    val id: Long,

    val nameTech: String,

    val dateTimeCreate: Long,

    val num: String,

    val latitude: String,

    val equipment: String,

    val typeTech: String,

    val addressName: String,

    val defect: String,

    val stage: String,

    val tel: String,

    val longitude: String,

    @Json(name = "date_time_exec")
    val dateTimeExec: List<EditDateTimeData>,

    val status: Int
) : Parcelable {

    companion object {

        fun OrderFullUIModel.toEditData(): EditOrderData =
            EditOrderData(
                this.id,
                this.nameTech,
                this.dateTimeCreate,
                this.num,
                this.latitude,
                this.equipment,
                this.typeTech,
                this.addressName,
                this.defect,
                this.stage,
                this.tel,
                this.longitude,
                this.dateTimeExec.map { it.toData() },
                this.status
            )
    }
}