package com.gravitygroup.avangard.interactors.settings

import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.interactors.settings.repository.SettingsRepository
import com.gravitygroup.avangard.presentation.menu.settings.data.TimeNotificationUIModel

class SettingsInteractor(
    private val repo: SettingsRepository
) {

    private val timesInSecondsList = listOf(
        300L,
        600L,
        900L,
        1800L,
        3600L,
        7200L,
        10800L,
        14400L,
        0L,
    )

    private var selectedTime = mutableListOf<Boolean>()

    private var timeNotificationLong = 0L

    fun updateTimeNotification(idTimeNotification: Int): List<TimeNotificationUIModel> {
        if (selectedTime.isEmpty()) {
            selectedTime.addAll(timesInSecondsList.map { false })
        }
        selectedTime[idTimeNotification] = selectedTime[idTimeNotification].not()
        val timeNotification = if (selectedTime[idTimeNotification]) {
            timesInSecondsList[idTimeNotification]
        } else {
            timesInSecondsList.last()
        }
        timeNotificationLong = timeNotification
        return repo
            .getTitleTimes()
            .mapIndexed { index, title ->
                val isEnabled = index == idTimeNotification
                TimeNotificationUIModel(index, title, isEnabled)
            }
    }

    suspend fun setupNotification(): RequestResult<EditData> {
        return repo.setupGeneralNotification(timeNotificationLong)
    }

    suspend fun setNotificationToken(token: String): RequestResult<EditData> {
        return repo.setNotificationToken(token)
    }

    fun saveTimeNotification() {
        repo.saveTimeNotification(timeNotificationLong)
    }

    fun getTimesNotification(): List<TimeNotificationUIModel> {
        val findIndex = timesInSecondsList
            .mapIndexed { index, time ->
                if (time == repo.getEnabledTime())
                    index
                else
                    INT_DEFAULT
            }
            .find { it > INT_DEFAULT }

        val enabledTime = findIndex ?: timesInSecondsList.size - 1

        return repo
            .getTitleTimes()
            .mapIndexed { index, title ->
                val isEnabled = index == enabledTime
                TimeNotificationUIModel(index, title, isEnabled)
            }
    }

    companion object {

        private const val INT_DEFAULT = 0
    }
}
