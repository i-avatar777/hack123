package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.core.utils.DateUtils.fullDateToLongTime
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DateTimeExecData(

	@Json(name="date_exec")
	val dateExec: String,

	@Json(name="time_start_exec")
	val timeStartExec: String,

	@Json(name="time_end_exec")
	val timeEndExec: String
) : Parcelable {

	companion object {

		fun DateTimeExecData.toUIModel(index: Int): DateTimeExecUIModel =
			DateTimeExecUIModel(
				index,
				"$dateExec $timeStartExec".fullDateToLongTime(),
				"$dateExec $timeEndExec".fullDateToLongTime(),
			)
	}
}
