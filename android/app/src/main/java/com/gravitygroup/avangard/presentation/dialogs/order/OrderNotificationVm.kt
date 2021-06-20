package com.gravitygroup.avangard.presentation.dialogs.order

import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import kotlinx.coroutines.launch

class OrderNotificationVm(
    private val authInteractor: AuthInteractor,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<OrderNotificationVm.OrderNotificationViewState>(handleState, OrderNotificationViewState) {

    fun setNotification(
        idOrder: Long,
        time: String,
        comment: String?,
        dismissAction: () -> Unit
    ) {
        launch {
            ordersInteractor.setNotify(idOrder, OrderNotificationRequest(time, comment))
                .handleResultWithError(
                    { dismissAction.invoke() },
                    { dismissAction.invoke() }
                )
        }
    }


    object OrderNotificationViewState : IViewModelState
}

data class OrderNotificationState(
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) : IViewModelState
