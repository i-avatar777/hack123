package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.presentation.order_info.CachedOrderUIModel
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Saved
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Started
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel.Companion.isEmpty
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel

class LocalOrdersRepository {

    private var shortOrdersList: List<OrderShortUIModel> = emptyList()
    private var cachedOrdersList: List<CachedOrderUIModel> = emptyList()

    private var editingOrder: EditingOrderUIModel = EditingOrderUIModel(
        Started,
        OrderFullUIModel.empty()
    )

    fun findFullCachedOrder(id: Long): CachedOrderUIModel? =
        cachedOrdersList.find { it.orderFullUIModel.id == id }

    fun getCachedFullOrders(): List<CachedOrderUIModel> =
        cachedOrdersList

    fun cacheStatusShortOrder(idOrder: Long, statusOrder: Int) {
        val newOrderList = mutableListOf<OrderShortUIModel>()
        shortOrdersList.forEachIndexed { index, orderDetailsData ->
            val newOrder = if (orderDetailsData.id == idOrder) {
                shortOrdersList[index].copy(status = statusOrder)
            } else {
                shortOrdersList[index]
            }
            newOrderList.add(newOrder)
        }
        shortOrdersList = newOrderList
    }

    fun cacheOrderInfo(idEditingOrder: Long, shortOrder: OrderShortUIModel, cacheOrder: CachedOrderUIModel) {
        shortOrdersList = shortOrdersList.map { orderShortUIModel ->
            if (orderShortUIModel.id == shortOrder.id) {
                shortOrder
            } else {
                orderShortUIModel
            }
        }

        if (cachedOrdersList.isNotEmpty()) {
            cachedOrdersList = cachedOrdersList.map { item ->
                if (item.orderFullUIModel.id == idEditingOrder) {
                    cacheOrder
                } else {
                    item
                }
            }
        }
        if (cachedOrdersList.contains(cacheOrder).not()) {
            cachedOrdersList = cachedOrdersList.plus(cacheOrder)
        }
    }

    fun cacheEditingOrder(editingOrderUIModel: EditingOrderUIModel) {
        editingOrder = editingOrderUIModel
    }

    fun getEditingOrder(): EditingOrderUIModel = editingOrder

    fun isEmptyEditing(): Boolean =
        (editingOrder.orderFullUIModel.isEmpty() && editingOrder.editingOrderType is Started) ||
                (editingOrder.orderFullUIModel.isEmpty().not() && editingOrder.editingOrderType is Saved)

    fun findShortCachedOrder(id: Long): OrderShortUIModel? =
        getCachedShortOrders().find { it.id == id }

    fun getCachedShortOrders(): List<OrderShortUIModel> = shortOrdersList

    fun saveShortOrders(seenOrders: List<OrderShortUIModel>) {
        shortOrdersList = seenOrders
    }
}