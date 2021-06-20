package com.gravitygroup.avangard.core.ext

import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDate
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel.Companion.isNotEmpty
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import java.util.Date

fun OrderFullUIModel.isCurrentDay(time: Date): Boolean =
    this.dateTimeExec
        .takeIf { it.isNotEmpty() }
        ?.first()?.start?.toSimpleDateString() == time.toSimpleDate()

fun OrderFullUIModel.hasExecInCurrentDay(time: Date): Boolean =
    this.dateTimeExec
        .find { exec ->
            time.toSimpleDate() == exec.start.toSimpleDateString()
        }
        ?.isNotEmpty()
        ?: false

fun OrderFullUIModel.hasNotTime(): Boolean =
    this.dateTimeExec
        .takeIf { it.isNotEmpty() }
        ?.first()?.end == DateTimeExecUIModel.LONG_DEFAULT

fun OrderFullUIModel.isNotTimeOrder(time: Date): Boolean =
    this.dateTimeExec.isEmpty() || (this.isCurrentDay(time) && this.hasNotTime())