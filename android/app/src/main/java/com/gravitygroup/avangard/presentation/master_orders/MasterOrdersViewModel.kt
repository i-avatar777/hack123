package com.gravitygroup.avangard.presentation.master_orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.core.ext.hasExecInCurrentDay
import com.gravitygroup.avangard.core.ext.isNotTimeOrder
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.utils.DateUtils.toHoursValue
import com.gravitygroup.avangard.core.utils.DateUtils.toMinutesValue
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDate
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.domain.orders.EventData
import com.gravitygroup.avangard.domain.orders.EventData.Companion.toEvent
import com.gravitygroup.avangard.domain.orders.EventStatus
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.InWork
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.filter.data.FilterOpenSpecs.Master
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.map.data.StartMapType.MasterOrders
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findName
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findType
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderFilterUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch
import java.util.Date

class MasterOrdersViewModel(
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<MasterOrdersState>(handleState, MasterOrdersState()) {

    private val directoriesLive: LiveData<DirectoryUIModel> = liveData {
        emit(ordersInteractor.getDirectories())
    }

    private val _noTimedEventsLiveData = MutableLiveData<List<OrderFullUIModel>>()
    private val noTimedEventsLiveData =
        MediatorLiveData<List<OrderFullUIModel>>().apply {
            val updateFunc = {
                var orders = _noTimedEventsLiveData.value
                val filterData = prefManager.ordersFilter
                val directories = directoriesLive.value
                if (directories != null) {
                    orders =
                        if (filterData != null) orders?.filter { order ->
                            filterData.matchFull(
                                order,
                                directories.nameTech.findName(order.nameTech),
                                directories.typeTech.findType(order.typeTech)
                            )
                        }
                        else orders
                    value = orders ?: emptyList()
                }
            }
            addSource(_noTimedEventsLiveData) { updateFunc() }
            addSource(prefManager.ordersFilterLive) {
                updateFilters()
                updateFunc()
            }
            addSource(directoriesLive) { updateFunc }
        }

    private val _timingEventsLiveData = MutableLiveData<List<OrderFullUIModel>>()
    private val timingEventsLiveData =
        MediatorLiveData<List<OrderFullUIModel>>().apply {
            val updateFunc = {
                var orders = _timingEventsLiveData.value
                val filterData = prefManager.ordersFilter
                val directories = directoriesLive.value
                if (directories != null) {
                    orders =
                        if (filterData != null) orders?.filter { order ->
                            filterData.matchFull(
                                order,
                                directories.nameTech.findName(order.nameTech),
                                directories.typeTech.findType(order.typeTech)
                            )
                        }
                        else orders
                    value = orders ?: emptyList()
                }
            }
            addSource(_timingEventsLiveData) { updateFunc() }
            addSource(prefManager.ordersFilterLive) {
                updateFilters()
                updateFunc()
            }
            addSource(directoriesLive) { updateFunc }
        }

    private val timingEventsObserver = Observer<List<OrderFullUIModel>> { orders ->
        updateTimingEvents(orders ?: emptyList())
    }

    private val noTimedEventsObserver = Observer<List<OrderFullUIModel>> { orders ->
        updateNoTimedEvents(orders ?: emptyList())
    }

    init {
        timingEventsLiveData.observeForever(timingEventsObserver)
        noTimedEventsLiveData.observeForever(noTimedEventsObserver)
    }

    fun navigateToFilterScreen() {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToFilter(Master)
            )
        )
    }

    fun navigateToNotifyOrder(orderId: Long) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToOrderNotification(
                    orderId
                )
            )
        )
    }

    fun navigateToMapScreen() {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToMapOrders(MasterOrders(getChosenDate()))
            )
        )
    }

    fun navigateToOrderInfo(idOrder: Long) {
        ordersInteractor.findShortCachedOrder(idOrder)?.also { shortOrder ->
            navigate(
                NavigationCommand.Dir(
                    NestedMainDirections.actionToOrderInfo(shortOrder)
                )
            )
        }
    }

    fun requestCalendarView() {
        val time = getChosenDate()
        updateState {
            it.copy(chosenDate = time, isLoading = true)
        }
        launch(Dispatchers.Main) {
            ordersInteractor.getOnMasterOrders().flatMapMerge {
                ordersInteractor.getActualFullOrders()
            }
                .collect { orderList ->
                    updateState { it.copy(isLoading = false) }

                    val timingEvents = orderList
                        .filter { order ->
                            order.dateTimeExec.isNotEmpty() && order.hasExecInCurrentDay(time) &&
                                    order.status == InWork.statusOrder
                        }
                    _timingEventsLiveData.value = timingEvents

                    val noTimedEvents = orderList
                        .filter { order ->
                            order.isNotTimeOrder(time) && order.status == InWork.statusOrder
                        }
                    _noTimedEventsLiveData.value = noTimedEvents
                }
        }
    }

    private fun updateNoTimedEvents(orders: List<OrderFullUIModel>?) {
        val events = orders
            ?.map { it.toEvent(getEventTitle(it)) }
            ?: emptyList()
        updateState {
            it.copy(
                noTimeEvents = events.toMutableList()
            )
        }
    }

    private fun updateTimingEvents(orders: List<OrderFullUIModel>?) {
        val events = orders
            ?.flatMap { order ->
                order.dateTimeExec
                    .filter { dateTimeExec ->
                        val isCurrentDay =
                            dateTimeExec.start.toSimpleDateString() == getChosenDate().toSimpleDate()
                        isCurrentDay
                    }
                    .map { dateTimeExec ->
                        val (startHour, startMinute) = dateTimeExec
                            .start
                            .let { it.toHoursValue() to it.toMinutesValue() }
                        val (endHour, endMinute) = dateTimeExec
                            .end
                            .let { it.toHoursValue() to it.toMinutesValue() }
                        val hoursDiff = endHour - startHour
                        val minuteDiff = if (startHour == endHour &&
                            (startMinute == endMinute) || (endMinute - startMinute < MINUTES_DIFF_DEFAULT)
                        ) {
                            MINUTES_DIFF_DEFAULT
                        } else {
                            endMinute - startMinute
                        }
                        val duration = hoursDiff * MINUTES_IN_HOUR + minuteDiff
                        EventData(
                            order.id,
                            getEventTitle(order),
                            startHour,
                            startMinute,
                            duration,
                            EventStatus.NOTSETPUSH
                        )
                    }
            } ?: emptyList()
        updateState {
            it.copy(
                timingEvents = events.toMutableList()
            )
        }
    }

    fun removeFilter(type: FilterTypeUIModel) {
        prefManager.resetFilterItem(type)
    }

    fun setupChosenDate(date: Date) {
        updateState {
            it.copy(
                chosenDate = date
            )
        }
    }

    fun resetChosenDate() {
        updateState {
            it.copy(
                chosenDate = Date(System.currentTimeMillis())
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timingEventsLiveData.removeObserver(timingEventsObserver)
        noTimedEventsLiveData.removeObserver(noTimedEventsObserver)
    }

    private fun getChosenDate(): Date {
        val currentDate = Date(System.currentTimeMillis())
        return if (currentState.chosenDate.toSimpleDate() != currentDate.toSimpleDate()) {
            currentState.chosenDate
        } else {
            currentDate
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

    private fun getEventTitle(order: OrderFullUIModel): String {
        val directories = directoriesLive.value ?: return ""
        return "${order.addressName} ${directories.typeTech.findType(order.typeTech)} ${order.defect}"
    }

    companion object {

        const val MINUTES_IN_HOUR = 60
        private const val MINUTES_DIFF_DEFAULT = 40
    }
}

data class MasterOrdersState(
    val isLoading: Boolean = false,
    val chosenDate: Date = Date(System.currentTimeMillis()),
    val filters: List<OrderFilterUIModel> = mutableListOf(),
    val timingEvents: MutableList<EventData> = mutableListOf(),
    val noTimeEvents: MutableList<EventData> = mutableListOf()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}
