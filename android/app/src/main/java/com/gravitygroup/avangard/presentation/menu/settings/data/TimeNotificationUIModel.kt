package com.gravitygroup.avangard.presentation.menu.settings.data

import com.gravitygroup.avangard.core.recycler.RecyclerViewItem

data class TimeNotificationUIModel(
    val id: Int,

    val title: String,

    val isEnabled: Boolean
) : RecyclerViewItem {

    override fun getId(): Any = "$id $title"
}