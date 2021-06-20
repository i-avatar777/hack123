package com.gravitygroup.avangard.presentation.map

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
import com.gravitygroup.avangard.presentation.map.MapOrdersVm.MapOrdersViewState
import com.gravitygroup.avangard.presentation.map.data.StartMapType
import com.gravitygroup.avangard.presentation.map.data.StartMapType.MasterOrders
import com.gravitygroup.avangard.presentation.map.data.StartMapType.OpenOrders
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapOrdersVm(
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<MapOrdersViewState>(handleState, MapOrdersViewState()) {

    private val _mapOrdersLiveData = MutableLiveData<List<OrderShortUIModel>>()
    val mapOrdersLiveData: MediatorLiveData<List<OrderShortUIModel>>
        get() {
            val mediator = MediatorLiveData<List<OrderShortUIModel>>()
            val updateOrdersFunc = {
                val orders = prefManager.ordersFilterLive.value?.let { filterData ->
                    _mapOrdersLiveData.value?.filter { filterData.match(it) }
                } ?: _mapOrdersLiveData.value
                mediator.value = orders ?: emptyList()
            }
            mediator.addSource(_mapOrdersLiveData) { updateOrdersFunc() }
            mediator.addSource(prefManager.ordersFilterLive) {
                updateFilters()
                updateOrdersFunc()
            }
            return mediator
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

    fun requestOrders(startMapType: StartMapType) {
        updateState {
            it.copy(isLoading = true, orders = emptyList())
        }
        when (startMapType) {
            is OpenOrders -> launch(Dispatchers.Main) {
                val list = ordersInteractor.getRemoteOpenOrders()
                updateState {
                    it.copy(isLoading = false)
                }
                _mapOrdersLiveData.value = list
            }
            is MasterOrders -> launch(Dispatchers.Main) {
                ordersInteractor.getDayMasterOrders(startMapType.currentDay)
                    .collect { list ->
                        updateState {
                            it.copy(isLoading = false)
                        }
                        _mapOrdersLiveData.value = list
                    }
            }
        }
    }

    fun onAddress(address: String) {
        if (address == currentState.selectedAddress) {
            return
        }
        updateState {
            it.copy(selectedAddress = address)
        }
    }

    fun removeFilter(type: FilterTypeUIModel) {
        prefManager.resetFilterItem(type)
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

    data class MapOrdersViewState(
        val isLoading: Boolean = false,
        val orders: List<OrderShortUIModel> = mutableListOf(),
        val selectedAddress: String = "",
        val filters: List<OrderFilterUIModel> = mutableListOf()
    ) : IViewModelState {

        override fun save(outState: SavedStateHandle) {
            super.save(outState)
        }

        override fun restore(savedState: SavedStateHandle): IViewModelState {
            return super.restore(savedState)
        }
    }
}