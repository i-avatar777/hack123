package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.core.utils.DateUtils.fullDateToLongTime
import com.gravitygroup.avangard.domain.orders.DateTimeExecData.Companion.toUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.utils.PhoneUtils
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderInfoData(

    @Json(name = "nametech")
    val nameTech: String,

    @Json(name = "datatime")
    val dataTime: String,

    @Json(name = "num")
    val num: String,

    @Json(name = "latitude")
    val latitude: String,

    @Json(name = "equipment")
    val equipment: String,

    @Json(name = "typetech")
    val typeTech: String,

    @Json(name = "adressname")
    val addressName: String,

    @Json(name = "defect")
    val defect: String,

    @Json(name = "stage")
    val stage: String,

    @Json(name = "date_time_exec")
    val dateTimeExec: List<DateTimeExecData>,

    @Json(name = "tel")
    val tel: String,

    @Json(name = "longitude")
    val longitude: String,

    @Json(name = "status")
    val status: Int
) : Parcelable {

    companion object {

        private const val EMPTY_STRING = ""

        fun OrderInfoData.toUIModel(id: Long): OrderFullUIModel {
            return OrderFullUIModel(
                id, nameTech, dataTime.fullDateToLongTime(), num, latitude, equipment, typeTech,
                addressName, defect, stage, PhoneUtils.formatPhoneNumber(tel) ?: EMPTY_STRING,
                longitude, dateTimeExec
                    .filter { data ->
                        data.dateExec.isNotEmpty() && data.timeEndExec.isNotEmpty() && data.timeStartExec.isNotEmpty()
                    }
                    .mapIndexed { index, dateTimeExecData ->
                        dateTimeExecData.toUIModel(index)
                    }, status
            )
        }

        fun OrderInfoData.toUIModel(id: Long, latitudeOut: String, longitudeOut: String): OrderFullUIModel {
            return OrderFullUIModel(
                id, nameTech, dataTime.fullDateToLongTime(), num, latitudeOut, equipment, typeTech,
                addressName, defect, stage, PhoneUtils.formatPhoneNumber(tel) ?: EMPTY_STRING,
                longitudeOut, dateTimeExec
                    .filter { data ->
                        data.dateExec.isNotEmpty() && data.timeEndExec.isNotEmpty() && data.timeStartExec.isNotEmpty()
                    }
                    .mapIndexed { index, dateTimeExecData ->
                        dateTimeExecData.toUIModel(index)
                    }, status
            )
        }
    }
}
