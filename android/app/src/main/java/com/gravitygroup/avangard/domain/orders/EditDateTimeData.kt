package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.core.utils.DateUtils.toTimeString
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditDateTimeData(
    @Json(name="date_exec")
    val dateExec: String,
    @Json(name="time_start_exec")
    val timeStartExec: String,
    @Json(name="time_end_exec")
    val timeEndExec: String
) : Parcelable {

    companion object {
        fun DateTimeExecUIModel.toData() : EditDateTimeData =
            EditDateTimeData(this.start.toSimpleDateString(), this.start.toTimeString(), this.end.toTimeString())
    }
}
