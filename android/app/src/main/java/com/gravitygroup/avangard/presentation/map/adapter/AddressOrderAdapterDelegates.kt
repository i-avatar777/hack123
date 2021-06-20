package com.gravitygroup.avangard.presentation.map.adapter

import com.gravitygroup.avangard.core.ext.renderState
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemAddressOrderBinding
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnOrderItemClick = (OrderShortUIModel) -> Unit
object AddressOrderAdapterDelegates {
    fun ordersDelegate(moreHandler: OnOrderItemClick) =
            adapterDelegateViewBinding<OrderShortUIModel, RecyclerViewItem, ItemAddressOrderBinding>(
                    { layoutInflater, root -> ItemAddressOrderBinding.inflate(layoutInflater, root, false) }
            ) {
                bind {
                    binding.apply {
                        orderInfoRoot.renderState(item)
                        orderMore.setSafeOnClickListener { moreHandler.invoke(item) }
                    }
                }
            }
}