package com.gravitygroup.avangard.presentation.dialogs.exit

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.core.utils.SingleLiveEvent
import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.*
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.Companion.toStatusOrderType
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.dialogs.exit.ConfirmDialogVm.ExitDialogViewState
import kotlinx.coroutines.launch

class ConfirmDialogVm(
    private val authInteractor: AuthInteractor,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<ExitDialogViewState>(handleState, ExitDialogViewState) {

    private val _statusOrderLiveData = SingleLiveEvent<StatusOrderType>()
    val statusOrderTypeLiveData: LiveData<StatusOrderType> = _statusOrderLiveData

    fun exitFromApp(dismissAction: () -> Unit) {
        launch {
            authInteractor.logout()
                .handleResultWithError(
                    {
                        dismissAction.invoke()
                        navigate(NavigationCommand.StartLogin)
                    }, {
                        dismissAction.invoke()
                        navigate(NavigationCommand.StartLogin)
                    }
                )
        }
    }

    fun requestStatus(idOrder: Long) {
        ordersInteractor.findShortCachedOrder(idOrder)?.also { shortOrder ->
            _statusOrderLiveData.value = shortOrder.status.toStatusOrderType()
        }
    }

    fun setupStatus(
        idOrder: Long,
        statusOrderType: StatusOrderType,
        dismissAction: () -> Unit
    ) {
        if (statusOrderType is Skip) {
            dismissAction.invoke()
        } else {
            launch {
                ordersInteractor.setupStatus(idOrder, statusOrderType.statusOrder)
                    .handleResultWithError(
                        {
                            _statusOrderLiveData.value = statusOrderType
                            ordersInteractor.changeStatusOrder(idOrder, statusOrderType.statusOrder)
                            dismissAction.invoke()
                            when (statusOrderType) {
                                InWork -> navigate(
                                    NavigationCommand.Dir(
                                        NestedMainDirections.actionToMasterOrders()
                                    )
                                )
                                Cancel -> navigateBack()
                                Complete ->
                                    ordersInteractor.findShortCachedOrder(idOrder)?.also { shortOrder ->
                                        navigate(
                                            NavigationCommand.Dir(
                                                NestedMainDirections.actionToOrderInfo(shortOrder)
                                            )
                                        )
                                    }
                                else -> {
                                }
                            }
                        }, {
                            dismissAction.invoke()
                        }
                    )
            }
        }
    }

    object ExitDialogViewState : IViewModelState
}

data class ConfirmState(
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) : IViewModelState