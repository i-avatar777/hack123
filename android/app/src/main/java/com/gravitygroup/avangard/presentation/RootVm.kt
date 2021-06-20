package com.gravitygroup.avangard.presentation

import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.SecurityPrefManager
import com.gravitygroup.avangard.core.network.errors.ApiError
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.utils.ConnectionData
import com.gravitygroup.avangard.utils.NetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.inject

class RootVm(
    private val context: Context,
    private val networkErrorBus: NetworkErrorBus,
    private val secManager: SecurityPrefManager,
    handleState: SavedStateHandle,
) : BaseViewModel<RootState>(handleState, RootState()) {

    private val networkMonitor: NetworkMonitor by inject()

    init {
        viewModelScope.launch {
            networkErrorBus.events
                .collect {
                    if (it is ApiError.TokenError) {
                        secManager.doLogout()
                        navigate(NavigationCommand.StartLogin)
                    }
                }
        }
    }

    fun onInit() {
        initObservers()
    }

    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }

    private fun initObservers() {
        networkMonitor.connection.observeForever(connectionObserver)
    }

    private fun removeObservers() {
        networkMonitor.connection.removeObserver(connectionObserver)
    }

    private val connectionObserver = Observer<ConnectionData> {
        if (it.isConnected.not()) {
            notify(Notify.Error(context.getString(R.string.error_no_network)))
        } else {
            notify(Notify.ErrorHide(context.getString(R.string.hint_access_to_network)))
        }
    }

}

data class RootState(
    val pendingCommand: NavigationCommand? = null
) : IViewModelState
