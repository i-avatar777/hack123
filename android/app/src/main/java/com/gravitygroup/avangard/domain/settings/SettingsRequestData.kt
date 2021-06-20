package com.gravitygroup.avangard.domain.settings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsRequestData(
    val uuid: String,
    val notification_time: String
) : Parcelable {

    companion object {

        private const val STRING_DEFAULT = ""

        fun NotificationUIModel.toRequest(time: String): SettingsRequestData =
            SettingsRequestData(
                this.deviceId,
                time
            )

        fun empty(): SettingsRequestData =
            SettingsRequestData(
                STRING_DEFAULT,
                STRING_DEFAULT
            )
    }
}