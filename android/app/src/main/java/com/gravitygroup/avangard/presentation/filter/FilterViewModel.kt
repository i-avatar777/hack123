package com.gravitygroup.avangard.presentation.filter

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.*
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogSpecs
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogUIModel
import com.gravitygroup.avangard.presentation.filter.data.OrdersFilterData

class FilterViewModel(
    private val context: Context,
    private val prefManager: PreferenceManager,
    ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<FilterState>(handleState, FilterState()) {

    init {
        prefManager.ordersFilter?.let { filterData ->
            updateState { it.copy(filterData = filterData) }
        }
    }

    fun onCatalogField(type: FilterCatalogItemType) {
        val checkedTech = when (type) {
            Type -> state.value?.let { it.filterData.typesTech } ?: emptyList()
            Name -> state.value?.let { it.filterData.namesTech } ?: emptyList()
            None -> emptyList()
        }
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToFilterCatalog(
                    FilterCatalogSpecs(type, checkedTech)
                )
            )
        )
    }

    fun onStartDate(startDate: Long) {
        val endDate = currentState.filterData.dateEnd
        when {
            endDate != null && startDate > endDate -> {
                notify(Notify.Error(context.getString(R.string.filter_start_more_end)))
            }
            else -> {
                val filterData = currentState.filterData.copy(
                    dateStart = startDate
                )
                updateState { it.copy(filterData = filterData) }
            }
        }
    }

    fun onEndDate(endDate: Long) {
        val startDate = currentState.filterData.dateStart
        when {
            startDate != null && endDate < startDate -> {
                notify(Notify.Error(context.getString(R.string.filter_end_less_start)))
            }
            else -> {
                val filterData = currentState.filterData.copy(
                    dateEnd = endDate
                )
                updateState { it.copy(filterData = filterData) }
            }
        }
    }

    fun onOrderNumber(number: String) {
        val filterData = state.value!!.filterData.copy(
            orderNumber = number
        )
        updateState { it.copy(filterData = filterData) }
    }

    fun onPhone(phone: String) {
        val filterData = state.value!!.filterData.copy(
            customerPhone = phone
        )
        updateState { it.copy(filterData = filterData) }
    }

    fun onCatalogData(typedData: Pair<FilterCatalogItemType, List<FilterCatalogUIModel>>) {
        val (type, data) = typedData
        val curFilterData = state.value!!.filterData
        val filterData = when (type) {
            Type -> curFilterData.copy(typesTech = data.takeIf { it.isNotEmpty() }?.map { it.title })
            Name -> curFilterData.copy(namesTech = data.takeIf { it.isNotEmpty() }?.map { it.title })
            else -> null
        }
        if (filterData != null) {
            updateState { it.copy(filterData = filterData) }
        }
    }

    fun onDone() {
        prefManager.ordersFilter = state.value!!.filterData
        navigateBack()
    }

    // TODO: в ордерах щас просто имена tech type / name, надо бы на id заменить
    //      здесь из справочника выбирать и соот-но в OrdersFilterData.match() поправить на ids
    /*private val techNameDirectory = ordersInteractor.getDirectories().nameTech.map { item -> item.toUIModel() }
    private val techTypeDirectory = ordersInteractor.getDirectories().typeTech.map { item -> item.toUIModel() }

    fun getTechName(id: Int?) =
        techNameDirectory.find { it.id == id }?.title ?: ""

    fun getTechType(id: Int?) =
        techTypeDirectory.find { it.id == id }?.title ?: ""*/
}

data class FilterState(
    val isLoading: Boolean = false,
    val filterData: OrdersFilterData = OrdersFilterData()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}