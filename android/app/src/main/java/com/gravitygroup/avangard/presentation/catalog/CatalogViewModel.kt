package com.gravitygroup.avangard.presentation.catalog

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType
import com.gravitygroup.avangard.presentation.catalog.data.CatalogItemType.*
import com.gravitygroup.avangard.presentation.catalog.data.CatalogUIModel
import com.gravitygroup.avangard.presentation.catalog.data.CatalogUIModel.Companion.toUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val ordersInteractor: OrdersInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<CatalogState>(handleState, CatalogState()) {

    fun requestFilterItem(type: CatalogItemType) {
        viewModelScope.launch {
            val directories = ordersInteractor.getDirectories()
            doFilterItem(directories, type)
        }
    }

    private fun doFilterItem(directories: DirectoryUIModel, type: CatalogItemType) {
        when (type) {
            NAME -> {
                updateState {
                    it.copy(
                        catalogType = type,
                        filterItem = directories.nameTech.map { item -> item.toUIModel() }
                    )
                }
            }
            TYPE -> {
                updateState {
                    it.copy(
                        catalogType = type,
                        filterItem = directories.typeTech.map { item -> item.toUIModel() }
                    )
                }
            }
            STATUS -> {
                updateState {
                    it.copy(
                        catalogType = type,
                        filterItem = context.resources.getStringArray(R.array.status_order_strings).asList()
                            .mapIndexed { index, string ->
                                CatalogUIModel(index.toString(), string ?: "")
                            }
                    )
                }
            }
            NONE -> {

            }
        }
    }
}

data class CatalogState(
    val isLoading: Boolean = false,
    val catalogType: CatalogItemType = NONE,
    val filterItem: List<CatalogUIModel> = emptyList()
) : IViewModelState