package com.gravitygroup.avangard.presentation.orders

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.filter.data.FilterOpenSpecs.Other
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.map.data.StartMapType.OpenOrders
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrdersVm(
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<OrdersState>(handleState, OrdersState()) {

    private val _openOrdersLiveData = MutableLiveData<List<OrderShortUIModel>>()
    val openOrdersLiveData: MediatorLiveData<List<OrderShortUIModel>>
        get() {
            val mediator = MediatorLiveData<List<OrderShortUIModel>>()
            val updateOrdersFunc = {
                var orders = _openOrdersLiveData.value
                val filterData = prefManager.ordersFilterLive.value
                orders =
                    if (filterData != null) orders?.filter { filterData.match(it) }
                    else orders
                mediator.value = orders ?: emptyList()
            }
            mediator.addSource(_openOrdersLiveData) { updateOrdersFunc() }
            mediator.addSource(prefManager.ordersFilterLive) {
                updateFilters()
                updateOrdersFunc()
            }
            return mediator
        }

    fun getOnSeenOrders() {
        updateData()
    }

    fun navigateToOrderInfoScreen(order: OrderShortUIModel) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToOrderInfo(order)
            )
        )
    }

    fun navigateToFilterScreen() {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToFilter(Other)
            )
        )
    }

    fun navigateToMapOrdersScreen() {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToMapOrders(OpenOrders)
            )
        )
    }

    fun removeFilter(type: FilterTypeUIModel) {
        prefManager.resetFilterItem(type)
    }

    private fun updateData() {
        updateState { it.copy(isLoading = true) }
        launch(Dispatchers.Main) {
            val list = ordersInteractor.getRemoteOpenOrders()
            updateState { it.copy(isLoading = false, isOrdersUpdated = true) }
            _openOrdersLiveData.value = list
        }
    }

    private fun updateFilters() {
        updateState {
            it.copy(
                filters = prefManager.ordersFilter?.getAssigned()?.mapIndexed { i, type ->
                    OrderFilterUIModel(i, type)
                } ?: listOf()
            )
        }
    }
}

data class OrdersState(
    val isLoading: Boolean = false,
    val isOrdersUpdated: Boolean = false,
    val orderDetails: List<OrderShortUIModel> = mutableListOf(),
    val filters: List<OrderFilterUIModel> = mutableListOf()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}
