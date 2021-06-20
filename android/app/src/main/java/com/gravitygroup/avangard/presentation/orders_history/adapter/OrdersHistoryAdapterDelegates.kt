package com.gravitygroup.avangard.presentation.orders_history.adapter

import androidx.core.view.isVisible
import com.gravitygroup.avangard.core.ext.renderState
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.DateUtils.toWordsDate
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemOrdersDateBinding
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel.Companion.toOrigin
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryItemType.Header
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryItemType.Info
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnOrderItemClick = (HistoryShortOrder) -> Unit

object OrdersHistoryAdapterDelegates {

    fun ordersDelegate(onOrderItemClick: OnOrderItemClick) =
        adapterDelegateViewBinding<HistoryShortOrder, RecyclerViewItem, ItemOrdersDateBinding>(
            { layoutInflater, root -> ItemOrdersDateBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    when (item.itemType) {
                        Info -> {
                            this.infoRoot.rootContainer.isVisible = true
                            this.infoRoot.renderState(item.toOrigin())
                            this.infoRoot.rootContainer.setSafeOnClickListener { onOrderItemClick.invoke(item) }
                        }
                        Header -> {
                            this.tvDate.isVisible = true
                            tvDate.text = item
                                .dateTimeCreate
                                .toWordsDate()
                        }
                    }
                }
            }
        }
}