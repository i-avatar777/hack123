package com.gravitygroup.avangard.presentation.order_edit.data

sealed class OrderDateExecType {

    object Correct: OrderDateExecType()

    object WorkTimesError: OrderDateExecType()

    object TimeGrowError: OrderDateExecType()

    object TimeStartMoreEnd: OrderDateExecType()

    object DateGrowError: OrderDateExecType()
}