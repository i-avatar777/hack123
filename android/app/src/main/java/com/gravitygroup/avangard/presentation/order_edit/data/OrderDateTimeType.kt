package com.gravitygroup.avangard.presentation.order_edit.data

sealed class OrderDateTimeType {

    object Date : OrderDateTimeType()

    object Time : OrderDateTimeType()
}