package com.gravitygroup.avangard.presentation.orders.adapter

import com.gravitygroup.avangard.core.ext.renderState
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemOrderBinding
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnOrderItemClick = (OrderShortUIModel) -> Unit
object OrderAdapterDelegates {
    fun ordersDelegate(onOrderItemClick: OnOrderItemClick) =
            adapterDelegateViewBinding<OrderShortUIModel, RecyclerViewItem, ItemOrderBinding>(
                    { layoutInflater, root -> ItemOrderBinding.inflate(layoutInflater, root, false) }
            ) {
                bind {
                    binding.apply {
                        renderState(item)
                        rootContainer.setSafeOnClickListener { onOrderItemClick.invoke(item) }
                    }
                }
            }
}
