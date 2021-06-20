package com.gravitygroup.avangard.interactors.orders.data

sealed class StatusOrder(
    val status: Int,
    val key: String
) {

    object Seen: StatusOrder(0, SEEN_ORDERS_KEY)

    object OnMaster: StatusOrder( 1, MASTER_ORDERS_KEY)

    companion object {
        private const val SEEN_ORDERS_KEY = "SEEN_ORDERS_KEY"
        private const val MASTER_ORDERS_KEY = "MASTER_ORDERS_KEY"
    }
}
