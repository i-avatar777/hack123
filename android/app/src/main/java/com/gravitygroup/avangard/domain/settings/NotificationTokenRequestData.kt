package com.gravitygroup.avangard.domain.settings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NotificationTokenRequestData(
    val uuid: String,
    val token_not: String
) : Parcelable {

    companion object {

        private const val STRING_DEFAULT = ""

        fun NotificationUIModel.toRequest(token: String): NotificationTokenRequestData =
            NotificationTokenRequestData(
                this.deviceId,
                token
            )

        fun empty(): SettingsRequestData =
            SettingsRequestData(
                STRING_DEFAULT,
                STRING_DEFAULT
            )
    }
}
