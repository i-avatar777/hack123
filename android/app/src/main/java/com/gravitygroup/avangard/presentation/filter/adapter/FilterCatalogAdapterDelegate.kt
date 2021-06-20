package com.gravitygroup.avangard.presentation.filter.adapter

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemFilterCatalogBinding
import com.gravitygroup.avangard.presentation.filter.data.FilterCatalogUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnCatalogItemClick = (FilterCatalogUIModel) -> Unit

object FilterCatalogAdapterDelegate {

    fun filterCatalogDelegate(onCatalogItemClick: OnCatalogItemClick) =
        adapterDelegateViewBinding<FilterCatalogUIModel, RecyclerViewItem, ItemFilterCatalogBinding>(
            { layoutInflater, root -> ItemFilterCatalogBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    cvCatalog.text = item.title
                    cvCatalog.isChecked = item.isChecked
                    cvCatalog.setSafeOnClickListener {
                        val newModel = item.copy(
                            isChecked = cvCatalog.isChecked
                        )
                        onCatalogItemClick.invoke(newModel)
                    }
                }
            }
        }
}