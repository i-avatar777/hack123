package com.gravitygroup.avangard.presentation.orders_history

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.filter.data.FilterOpenSpecs.Other
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel.Companion.toOrigin
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrdersHistoryVm(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<OrdersHistoryState>(handleState, OrdersHistoryState()) {

    fun navigateToFilterScreen() {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToFilter(Other)
            )
        )
    }

    fun navigateToOrderInfoScreen(historyShortOrder: HistoryShortOrder) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToOrderInfo(historyShortOrder.toOrigin())
            )
        )
    }

    fun getCompleteOrders() {
        launch(Dispatchers.Main) {
            ordersInteractor.getHistoryOrders()
                .collect { historyItemList ->
                    updateState {
                        it.copy(
                            orders = historyItemList
                        )
                    }
                }
        }
    }
}

data class OrdersHistoryState(
    val isLoading: Boolean = false,
    val orders: List<HistoryShortOrder> = mutableListOf()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}