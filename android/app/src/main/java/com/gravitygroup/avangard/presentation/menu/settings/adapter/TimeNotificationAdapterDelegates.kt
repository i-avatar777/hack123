package com.gravitygroup.avangard.presentation.menu.settings.adapter

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemInteractiveButtonBinding
import com.gravitygroup.avangard.presentation.menu.settings.data.TimeNotificationUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnFieldClick = (Int) -> Unit

object TimeNotificationAdapterDelegates {

    fun timeNotificationDelegate(onTimeFieldClick: OnFieldClick) =
        adapterDelegateViewBinding<TimeNotificationUIModel, RecyclerViewItem, ItemInteractiveButtonBinding>(
            { layoutInflater, root -> ItemInteractiveButtonBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    interactiveButton.apply {
                        text = item.title
                        isSelected = item.isEnabled
                        setSafeOnClickListener {
                            onTimeFieldClick.invoke(item.id)
                        }
                    }
                }
            }
        }
}