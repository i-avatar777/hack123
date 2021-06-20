package com.gravitygroup.avangard.interactors.settings

import com.gravitygroup.avangard.core.network.base.BaseResponseObj
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.settings.NotificationTokenRequestData
import com.gravitygroup.avangard.domain.settings.SettingsRequestData
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface SettingsApi {

    @POST("notificationGeneral")
    suspend fun setupGeneralNotification(
        @Body request: SettingsRequestData
    ) : BaseResponseObj<EditData>

    @PUT("notification")
    suspend fun setNotificationToken(
        @Body request: NotificationTokenRequestData
    ) : BaseResponseObj<EditData>

}
