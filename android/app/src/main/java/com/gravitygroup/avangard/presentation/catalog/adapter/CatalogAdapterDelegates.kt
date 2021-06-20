package com.gravitygroup.avangard.presentation.catalog.adapter

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemCatalogBinding
import com.gravitygroup.avangard.presentation.catalog.data.CatalogUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnCatalogItemClick = (CatalogUIModel) -> Unit

object CatalogAdapterDelegates {
    fun catalogDelegate(onCatalogItemClick: OnCatalogItemClick) =
            adapterDelegateViewBinding<CatalogUIModel, RecyclerViewItem, ItemCatalogBinding>(
                    { layoutInflater, root -> ItemCatalogBinding.inflate(layoutInflater, root, false) }
            ) {
                bind {
                    binding.apply {
                        tvCatalogItemName.text = item.title

                        tvCatalogItemName.setSafeOnClickListener { onCatalogItemClick.invoke(item) }
                    }
                }
            }
}
