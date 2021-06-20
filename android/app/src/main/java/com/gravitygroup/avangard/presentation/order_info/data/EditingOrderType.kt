package com.gravitygroup.avangard.presentation.order_info.data

sealed class EditingOrderType {

    object Started: EditingOrderType()

    object Editing : EditingOrderType()

    object Saved: EditingOrderType()
}