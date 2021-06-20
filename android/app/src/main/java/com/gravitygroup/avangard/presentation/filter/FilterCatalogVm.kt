package com.gravitygroup.avangard.presentation.filter

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogItemType.*
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogSpecs
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogUIModel
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogUIModel.Companion.toUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import kotlinx.coroutines.launch

class FilterCatalogVm(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<FilterCatalogState>(handleState, FilterCatalogState()) {

    fun requestFilterItem(specs: FilterCatalogSpecs) {
        viewModelScope.launch {
            val directories = ordersInteractor.getDirectories()
            doFilterItem(directories, specs)
        }
    }

    private fun doFilterItem(directories: DirectoryUIModel, specs: FilterCatalogSpecs) {
        when (specs.type) {
            Name ->
                updateState {
                    it.copy(
                        catalogType = specs.type,
                        filterItem = specs.checkedItems
                            .takeIf { listChecked -> listChecked.isNotEmpty() }
                            ?.let { checkedItems ->
                                directories.nameTech.map { name ->
                                    checkedItems
                                        .find { checkedItem -> checkedItem.equals(name.nameSpec, true) }
                                        ?.let { name.toUIModel(specs.type, true) } ?: name.toUIModel(specs.type)
                                }
                            }
                            ?: directories.nameTech.map { item -> item.toUIModel(specs.type) }
                    )
                }
            Type ->
                updateState {
                    it.copy(
                        catalogType = specs.type,
                        filterItem = specs.checkedItems
                            .takeIf { listChecked -> listChecked.isNotEmpty() }
                            ?.let { checkedItems ->
                                directories.typeTech.map { type ->
                                    checkedItems
                                        .find { checkedItem -> checkedItem.equals(type.techSpec, true) }
                                        ?.let { type.toUIModel(specs.type, true) } ?: type.toUIModel(specs.type)
                                }
                            }
                            ?: directories.typeTech.map { item -> item.toUIModel(specs.type) }
                    )
                }
            None -> {
            }
        }
    }

    fun setupFilterCatalog(filterCatalog: FilterCatalogUIModel) {
        updateState { state ->
            state.copy(
                filterItem = state.filterItem.map { item ->
                    if (item.id == filterCatalog.id) {
                        filterCatalog
                    } else {
                        item
                    }
                }
            )
        }
    }
}

data class FilterCatalogState(
    val isLoading: Boolean = false,
    val catalogType: FilterCatalogItemType = None,
    val filterItem: List<FilterCatalogUIModel> = emptyList()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}