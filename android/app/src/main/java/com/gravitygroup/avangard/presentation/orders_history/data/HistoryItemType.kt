package com.gravitygroup.avangard.presentation.orders_history.data

sealed class HistoryItemType {
    object Info : HistoryItemType()
    object Header : HistoryItemType()
}