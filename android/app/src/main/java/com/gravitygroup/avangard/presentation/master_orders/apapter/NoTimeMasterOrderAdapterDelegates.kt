package com.gravitygroup.avangard.presentation.master_orders.apapter

import androidx.core.view.isVisible
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.EventViewBinding
import com.gravitygroup.avangard.domain.orders.EventData
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnFieldClick = (Long) -> Unit

object NoTimeMasterOrderAdapterDelegates {
    fun noTimeOrderDelegate(onTimeFieldClick: OnFieldClick, onOrderFieldClick: OnFieldClick) =
        adapterDelegateViewBinding<EventData, RecyclerViewItem, EventViewBinding>(
            { layoutInflater, root -> EventViewBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    ivClock.isVisible = false
                    tvText.text = item.title
                    ivClock.setSafeOnClickListener {
                        onTimeFieldClick.invoke(item.id)
                    }
                    tvText.setSafeOnClickListener {
                        onOrderFieldClick.invoke(item.id)
                    }
                }
            }
        }
}
