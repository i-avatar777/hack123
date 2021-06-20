package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.domain.image.GetAttachmentData
import com.gravitygroup.avangard.domain.image.SendAttachmentList
import com.gravitygroup.avangard.domain.orders.DirectoryData
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.orders.OrderDetailsData
import com.gravitygroup.avangard.domain.orders.OrderInfoData
import com.gravitygroup.avangard.interactors.orders.data.StatusOrder
import com.gravitygroup.avangard.presentation.dialogs.order.OrderNotificationRequest
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel

interface RemoteOrdersRepository {

    suspend fun ordersByStatus(statusOrder: StatusOrder): RequestResult<List<OrderDetailsData>>

    suspend fun orderInfo(id: Long): RequestResult<OrderInfoData>

    suspend fun editOrder(orderFull: OrderFullUIModel): RequestResult<EditData>

    suspend fun getDirectory(): RequestResult<DirectoryData>

    suspend fun getAttachments(id: Long): RequestResult<List<GetAttachmentData>>

    suspend fun saveAttachments(idOrder: Long, queryImages: SendAttachmentList): RequestResult<List<GetAttachmentData>>

    suspend fun deleteAttachment(idOrder: Long): RequestResult<EditData>

    suspend fun setupStatus(idOrder: Long, statusOrder: Int): RequestResult<EditData>

    suspend fun setNotify(idOrder: Long, orderNotificationRequest: OrderNotificationRequest): RequestResult<Any>
}
