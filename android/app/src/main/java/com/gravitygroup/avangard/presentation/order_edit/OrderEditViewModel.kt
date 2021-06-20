package com.gravitygroup.avangard.presentation.order_edit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.core.network.errors.ApiError.BreakDataError
import com.gravitygroup.avangard.core.utils.DateUtils.toHoursValue
import com.gravitygroup.avangard.core.utils.DateUtils.toMinutesValue
import com.gravitygroup.avangard.core.utils.SingleLiveEvent
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.catalog.data.CatalogSpecs
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel.Name
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel.Type
import com.gravitygroup.avangard.presentation.filter.data.FilterUIModel
import com.gravitygroup.avangard.presentation.order_edit.OrderEditFragment.CachedViewOrderState
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateExecType.*
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateTimeType
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateTimeType.Date
import com.gravitygroup.avangard.presentation.order_edit.data.OrderDateTimeType.Time
import com.gravitygroup.avangard.presentation.order_info.data.*
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.emptyUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findName
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findType
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Editing
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Saved
import kotlinx.coroutines.launch
import timber.log.Timber

class OrderEditViewModel(
    private val context: Context,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<OrderEditState>(handleState, OrderEditState()) {

    private val _showTimeError = SingleLiveEvent<OrderDateExecType>()
    val showTimeSetupError: LiveData<OrderDateExecType> get() = _showTimeError

    init {
        viewModelScope.launch {
            val directories = ordersInteractor.getDirectories()
            updateState { it.copy(directories = directories) }
        }
    }

    fun addDatesToOrder() {
        val orderDates = state.value!!.orderDates
        orderDates.add(DateTimeExecUIModel.empty())
        updateState { it.copy(orderDates = orderDates) }
    }

    fun updateWorkDateTime(
        type: OrderDateTimeType,
        orderDate: DateTimeExecUIModel,
        position: Int
    ) {
        when (val state = checkOrderDateExec(type, orderDate, position)) {
            Correct -> updateDateTime(orderDate, position)
            else -> _showTimeError.value = state
        }
    }

    fun removeWorkDateTime(orderDate: DateTimeExecUIModel, position: Int) {
        val orderDates = state.value!!.orderDates
        orderDates.removeAt(position)
        updateState { it.copy(orderDates = orderDates) }
    }

    fun navigateToCatalog(specs: CatalogSpecs) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToCatalog(specs)
            )
        )
    }

    fun fillState(dateTimeExec: List<DateTimeExecUIModel>) {
        updateState {
            it.copy(orderDates = dateTimeExec.toMutableList())
        }
    }

    fun updateCategoryOrder(filter: FilterUIModel) {
        filter.type
            .takeIf { (it is Type || it is Name) && state.value != null }
            ?.also {
                val order = when (filter.type) {
                    is Type -> state.value?.orderFullUIModel?.copy(
                        typeTech = filter.item.id
                    )
                    is Name -> state.value?.orderFullUIModel?.copy(
                        nameTech = filter.item.id
                    )
                    else -> null
                }
                order?.also { safeOrder ->
                    val nameId = safeOrder.nameTech
                    val typeId = safeOrder.typeTech
                    val name = state.value
                        ?.directories
                        ?.nameTech
                        ?.findName(nameId)
                        ?.let {
                            NameTechUIModel(
                                it,
                                nameId
                            )
                        } ?: NameTechUIModel.empty()
                    val type = state.value
                        ?.directories
                        ?.typeTech
                        ?.findType(typeId)
                        ?.let {
                            TypeTechUIModel(
                                it,
                                typeId
                            )
                        } ?: TypeTechUIModel.empty()
                    ordersInteractor.cacheEditingOrder(
                        EditingOrderUIModel(
                            Editing,
                            safeOrder,
                            type,
                            name,
                        )
                    )
                }
            }
    }

    fun initialOrder(order: OrderFullUIModel) {
        if (ordersInteractor.isEmptyEditing()) {
            updateState {
                it.copy(orderFullUIModel = order)
            }
        } else {
            updateState {
                it.copy(orderFullUIModel = ordersInteractor.getEditingOrder().orderFullUIModel)
            }
        }
    }

    fun editOrderInfo(orderFull: OrderFullUIModel) {
        updateState { it.copy(isLoading = true) }

        launch {
            ordersInteractor.getEditingOrder().also { editingOrder ->
                val allOrder = state.value?.orderDates?.let { list ->
                    orderFull.copy(
                        dateTimeExec = list,
                        typeTech = getIdTech(editingOrder.typeTech.idTech, orderFull.typeTech),
                        nameTech = getIdTech(editingOrder.nameTech.idName, orderFull.nameTech)
                    )
                } ?: orderFull

                ordersInteractor.cacheEditingOrder(
                    ordersInteractor.getEditingOrder().copy(
                        orderFullUIModel = allOrder,
                        typeTech = getTypeTechUIModel(editingOrder.typeTech, orderFull.typeTech),
                        nameTech = getNameTechUIModel(editingOrder.nameTech, orderFull.nameTech)
                    )
                )
            }

            ordersInteractor.editOrderInfo(ordersInteractor.getEditingOrder().orderFullUIModel)
                .handleResultWithError({ _ ->
                    ordersInteractor.getEditingOrder().also { savedOrder ->
                        ordersInteractor.cacheEditingOrder(
                            EditingOrderUIModel(
                                Saved,
                                savedOrder.orderFullUIModel
                            )
                        )
                        ordersInteractor.cachedOrderInfo(savedOrder)
                    }
                    updateState { it.copy(isLoading = false) }
                    navigateBack()
                }, { throwable ->
                    updateState { it.copy(isLoading = false) }
                    mapAndShowError(BreakDataError("Incorrect data"))
                    Timber.e("observeEditOrderError: $throwable")
                })
        }
    }

    fun cacheViewState(orderViewState: CachedViewOrderState) {
        updateState {
            val cachedViewState = it.orderFullUIModel.copy(
                num = orderViewState.num,
                addressName = orderViewState.addressName,
                equipment = orderViewState.equipment,
                defect = orderViewState.defect,
                stage = orderViewState.stage,
                tel = orderViewState.tel,
            )
            it.copy(
                orderFullUIModel = cachedViewState
            )
        }
        saveState()
    }

    private fun updateDateTime(
        orderDate: DateTimeExecUIModel,
        position: Int
    ) {
        val orderDates = currentState.orderDates
        orderDates[position] = orderDate
        updateState { it.copy(orderDates = orderDates) }
    }

    private fun getIdTech(editingIdTech: String, sourceIdTech: String): String =
        when {
            editingIdTech.isNotEmpty() && editingIdTech != sourceIdTech -> editingIdTech
            else -> sourceIdTech
        }

    private suspend fun getTypeTechUIModel(editingTypeTech: TypeTechUIModel, sourceIdTech: String): TypeTechUIModel =
        when (editingTypeTech.idTech) {
            getIdTech(editingTypeTech.idTech, sourceIdTech) -> editingTypeTech
            else -> TypeTechUIModel(
                ordersInteractor.getDirectories().typeTech.findType(sourceIdTech),
                sourceIdTech
            )
        }

    private suspend fun getNameTechUIModel(editingNameTech: NameTechUIModel, sourceNameId: String): NameTechUIModel =
        when (editingNameTech.idName) {
            getIdTech(editingNameTech.idName, sourceNameId) -> editingNameTech
            else -> NameTechUIModel(
                ordersInteractor.getDirectories().nameTech.findName(sourceNameId),
                sourceNameId
            )
        }

    private fun checkOrderDateExec(
        typeDateTime: OrderDateTimeType,
        orderDate: DateTimeExecUIModel,
        position: Int
    ): OrderDateExecType {
        val isWorkTimesCorrect = verifyWorkTimes(orderDate.start, orderDate.end)
        val isTimeGrowCorrect = verifyGrowNeighboringTimesExec(orderDate.start, position)
        val isTimeStartMoreEnd = orderDate.start > orderDate.end
        val isDatesGrowCorrect = verifyGrowNeighboringDatesExec(orderDate.start, position)
        return when {
            typeDateTime is Time && isWorkTimesCorrect.not() -> WorkTimesError
            typeDateTime is Time && isTimeGrowCorrect.not() -> TimeGrowError
            typeDateTime is Time && isTimeStartMoreEnd -> TimeStartMoreEnd
            typeDateTime is Date && isDatesGrowCorrect.not() -> DateGrowError
            else -> Correct
        }
    }

    private fun verifyGrowNeighboringDatesExec(editDate: Long, position: Int): Boolean {
        return if (position > FIRST_TIME_EXEC_POS) {
            val prevTimeExec = currentState.orderDates[position - 1]
            prevTimeExec.end < editDate
        } else {
            true
        }
    }

    private fun verifyWorkTimes(start: Long, end: Long): Boolean {
        val startTime = start.toHoursValue() to start.toMinutesValue()
        val endHours = end.toHoursValue() to end.toMinutesValue()
        return checkTimeOnInterval(startTime) && checkTimeOnInterval(endHours)
    }

    private fun verifyGrowNeighboringTimesExec(start: Long, position: Int): Boolean {
        return if (position > FIRST_TIME_EXEC_POS) {
            val prevTimeExec = currentState.orderDates[position - 1]
            prevTimeExec.end < start
        } else {
            true
        }
    }

    private fun checkTimeOnInterval(time: Pair<Int, Int>): Boolean {
        val (hours, minutes) = time
        return (hours >= HOURS_START_INTERVAL && minutes >= MINUTES_START_INTERVAL) &&
                (hours <= HOURS_END_INTERVAL && minutes <= MINUTES_END_INTERVAL)
    }

    companion object {

        private const val FIRST_TIME_EXEC_POS = 0

        private const val HOURS_START_INTERVAL = 8
        private const val MINUTES_START_INTERVAL = 0
        private const val HOURS_END_INTERVAL = 22
        private const val MINUTES_END_INTERVAL = 59
    }
}

data class OrderEditState(
    val isLoading: Boolean = false,
    val orderFullUIModel: OrderFullUIModel = OrderFullUIModel.empty(),
    val directories: DirectoryUIModel = emptyUIModel(),
    val orderDates: MutableList<DateTimeExecUIModel> = mutableListOf()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}
