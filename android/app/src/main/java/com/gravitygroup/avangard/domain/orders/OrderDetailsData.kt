package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.core.utils.DateUtils.dateCreateToLongTime
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.utils.PhoneUtils
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDetailsData(

    @Json(name = "nametech")
    val nameTech: String,

    @Json(name = "datatime")
    val dateTime: String,

    @Json(name = "num")
    val num: String,

    @Json(name = "latitude")
    val latitude: String,

    @Json(name = "tel")
    val tel: String,

    @Json(name = "id")
    val id: Long,

    @Json(name = "typetech")
    val typeTech: String,

    @Json(name = "adressname")
    val addressName: String,

    @Json(name = "longitude")
    val longitude: String,

    @Json(name = "status")
    val status: Int
) : Parcelable {

    companion object {

        private const val EMPTY_STRING = ""

        fun OrderDetailsData.toUIModel(typeTech: String, nameTech: String): OrderShortUIModel =
            OrderShortUIModel(
                id, dateTime.dateCreateToLongTime(), num, latitude, PhoneUtils.formatPhoneNumber(tel) ?: EMPTY_STRING,
                typeTech, nameTech, addressName, longitude, status
            )

        fun OrderDetailsData.toUIModel(
            typeTech: String,
            nameTech: String,
            latitudeOut: String,
            longitudeOut: String
        ): OrderShortUIModel =
            OrderShortUIModel(
                id, dateTime.dateCreateToLongTime(), num, latitudeOut, PhoneUtils.formatPhoneNumber(tel) ?: EMPTY_STRING,
                typeTech, nameTech, addressName, longitudeOut, status
            )
    }
}