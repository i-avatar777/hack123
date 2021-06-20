package com.gravitygroup.avangard.interactors.orders.data

import com.gravitygroup.avangard.presentation.map.data.PointActiveState

sealed class StatusOrderType(
    val statusOrder: Int
) {

    object Open : StatusOrderType(OPEN_ORDER)

    object InWork : StatusOrderType(IN_WORK_ORDER)

    object Waiting : StatusOrderType(WAITING_ORDER)

    object Ready : StatusOrderType(READY_ORDER)

    object Complete : StatusOrderType(COMPLETE_ORDER)

    object Cancel : StatusOrderType(CANCEL_ORDER)

    object Fail : StatusOrderType(-1)

    object Skip : StatusOrderType(100)

    companion object {

        const val OPEN_ORDER = 0
        const val IN_WORK_ORDER = 1
        const val WAITING_ORDER = 2
        const val READY_ORDER = 3
        const val COMPLETE_ORDER = 4
        const val CANCEL_ORDER = 5

        fun Int.toStatusOrderType(): StatusOrderType =
            when(this) {
                OPEN_ORDER -> Open
                IN_WORK_ORDER -> InWork
                WAITING_ORDER -> Waiting
                READY_ORDER -> Ready
                COMPLETE_ORDER -> Complete
                CANCEL_ORDER -> Cancel
                else -> Fail
            }

        fun Int.toPointMapState(): PointActiveState =
            when(this) {
                OPEN_ORDER -> PointActiveState.Base
                IN_WORK_ORDER -> PointActiveState.Active
                COMPLETE_ORDER -> PointActiveState.Inactive
                else -> PointActiveState.Base
            }
    }
}