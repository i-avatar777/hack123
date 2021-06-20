package com.gravitygroup.avangard.presentation.order_info.data

import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateTimeExecUIModel(
    val index: Int,

    val start: Long,

    val end: Long
) : RecyclerViewItem, Parcelable {

    override fun getId(): String =
        "$start $end $index"

    companion object {

        private const val INT_DEFAULT = 0

        const val LONG_DEFAULT = 0L

        fun DateTimeExecUIModel.isNotEmpty(): Boolean = start != LONG_DEFAULT && end != LONG_DEFAULT

        fun empty(): DateTimeExecUIModel =
            DateTimeExecUIModel(INT_DEFAULT, LONG_DEFAULT, LONG_DEFAULT)
    }
}