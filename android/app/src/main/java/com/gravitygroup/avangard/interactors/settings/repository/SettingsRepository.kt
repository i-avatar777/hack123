package com.gravitygroup.avangard.interactors.settings.repository

import android.content.Context
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.network.base.BaseRequestResultHandler
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.core.network.errors.ErrorMapper
import com.gravitygroup.avangard.core.utils.DateUtils.secondsToFullTime
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.settings.NotificationTokenRequestData
import com.gravitygroup.avangard.domain.settings.SettingsRequestData
import com.gravitygroup.avangard.interactors.settings.SettingsApi

class SettingsRepository(
    dispatchersProvider: DispatchersProvider,
    errorMapper: ErrorMapper,
    networkErrorBus: NetworkErrorBus,
    private val settingsApi: SettingsApi,
    private val context: Context,
    private val prefManager: PreferenceManager,
) : BaseRequestResultHandler(dispatchersProvider, errorMapper, networkErrorBus) {

    suspend fun setupGeneralNotification(timeNotification: Long): RequestResult<EditData> =
        call {
            settingsApi.setupGeneralNotification(
                SettingsRequestData(
                    prefManager.deviceToken,
                    timeNotification.secondsToFullTime()
                )
            )
        }

    suspend fun setNotificationToken(token: String): RequestResult<EditData> =
        call {
            settingsApi.setNotificationToken(
                NotificationTokenRequestData(
                    prefManager.deviceToken,
                    token
                )
            )
        }

    fun getTitleTimes() = context.resources
        .getStringArray(R.array.notification_time_strings)
        .asList()

    fun getEnabledTime(): Long = prefManager.timeNotification

    fun saveTimeNotification(time: Long) {
        prefManager.timeNotification = time
    }
}
