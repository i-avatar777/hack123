package com.gravitygroup.avangard.presentation.menu.settings

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.network.errors.ApiError
import com.gravitygroup.avangard.interactors.settings.SettingsInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.menu.settings.SettingsVm.SettingViewState
import com.gravitygroup.avangard.presentation.menu.settings.data.TimeNotificationUIModel
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsVm(
    private val context: Context,
    handle: SavedStateHandle,
    private val settingsInteractor: SettingsInteractor
) : BaseViewModel<SettingViewState>(handle, SettingViewState()) {

    fun changeTimeNotification(idTimeNotification: Int) {
        val updatedTimesList = settingsInteractor.updateTimeNotification(idTimeNotification)
        updateState {
            it.copy(
                timesUIModelList = updatedTimesList
            )
        }
    }

    fun setupTimeNotification() {
        launch {
            settingsInteractor.setupNotification()
                .handleResultWithError({
                    notify(Notify.ErrorHide(context.getString(R.string.notification_success)))
                    saveTimeNotification()
                }, {
                    when (it) {
                        is ApiError.GeneralError, is ApiError.TokenError -> mapAndShowError(it)
                    }
                    Timber.e("error $it")
                })
        }
    }

    fun requestPositionTimeNotification() {
        updateState {
            it.copy(
                timesUIModelList = settingsInteractor.getTimesNotification()
            )
        }
    }

    private fun saveTimeNotification() {
        settingsInteractor.saveTimeNotification()
    }

    data class SettingViewState(
        val timesUIModelList: List<TimeNotificationUIModel> = emptyList()
    ) : IViewModelState
}