package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.network.base.BaseRequestResultHandler
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.core.network.errors.ErrorMapper
import com.gravitygroup.avangard.domain.image.GetAttachmentData
import com.gravitygroup.avangard.domain.image.SendAttachmentList
import com.gravitygroup.avangard.domain.orders.DirectoryData
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.orders.EditOrderData.Companion.toEditData
import com.gravitygroup.avangard.domain.orders.OrderDetailsData
import com.gravitygroup.avangard.domain.orders.OrderInfoData
import com.gravitygroup.avangard.interactors.orders.OrdersApi
import com.gravitygroup.avangard.interactors.orders.data.StatusOrder
import com.gravitygroup.avangard.presentation.dialogs.order.OrderNotificationRequest
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel

class RemoteOrdersRepositoryImpl(
    dispatchersProvider: DispatchersProvider,
    errorMapper: ErrorMapper,
    networkErrorBus: NetworkErrorBus,
    private val api: OrdersApi,
) : RemoteOrdersRepository, BaseRequestResultHandler(dispatchersProvider, errorMapper, networkErrorBus) {

    override suspend fun ordersByStatus(statusOrder: StatusOrder): RequestResult<List<OrderDetailsData>> =
        call {
            api.ordersByStatus(statusOrder.status)
        }

    override suspend fun orderInfo(id: Long): RequestResult<OrderInfoData> =
        call {
            api.orderById(id)
        }

    override suspend fun editOrder(orderFull: OrderFullUIModel): RequestResult<EditData> =
        call {
            api.editOrder(orderFull.id, orderFull.toEditData())
        }

    override suspend fun setupStatus(idOrder: Long, statusOrder: Int): RequestResult<EditData> =
        call {
            api.setupStatus(idOrder, statusOrder)
        }

    override suspend fun getDirectory(): RequestResult<DirectoryData> =
        call {
            api.getDirectory()
        }

    override suspend fun getAttachments(id: Long): RequestResult<List<GetAttachmentData>> =
        call {
            api.getAttachments(id)
        }

    override suspend fun saveAttachments(idOrder: Long, queryImages: SendAttachmentList):
        RequestResult<List<GetAttachmentData>> =
        call {
            api.saveAttachments(idOrder, queryImages)
        }

    override suspend fun deleteAttachment(idOrder: Long): RequestResult<EditData> =
        call {
            api.deleteAttachment(idOrder)
        }

    override suspend fun setNotify(idOrder: Long, orderNotificationRequest: OrderNotificationRequest): RequestResult<Any> =
        call {
            api.setNotify(idOrder, orderNotificationRequest)
        }
}
