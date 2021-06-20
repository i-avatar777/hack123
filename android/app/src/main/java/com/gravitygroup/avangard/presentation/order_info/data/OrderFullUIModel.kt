package com.gravitygroup.avangard.presentation.order_info.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderFullUIModel(

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

    val dateTimeExec: List<DateTimeExecUIModel>,

    val status: Int
) : Parcelable {

    companion object {

        private const val EMPTY_STRING = ""
        private const val DEFAULT_INT = 0
        private const val DEFAULT_LONG = 0L

        fun empty(): OrderFullUIModel =
            OrderFullUIModel(
                DEFAULT_LONG,
                EMPTY_STRING,
                DEFAULT_LONG,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                emptyList(),
                DEFAULT_INT
            )

        fun OrderFullUIModel.isEmpty(): Boolean =
            this.id == DEFAULT_LONG && this.nameTech == EMPTY_STRING
    }
}