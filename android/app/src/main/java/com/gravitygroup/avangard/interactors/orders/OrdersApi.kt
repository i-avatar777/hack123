package com.gravitygroup.avangard.interactors.orders

import com.gravitygroup.avangard.core.network.base.BaseResponseObj
import com.gravitygroup.avangard.domain.image.GetAttachmentData
import com.gravitygroup.avangard.domain.image.SendAttachmentList
import com.gravitygroup.avangard.domain.orders.DirectoryData
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.orders.EditOrderData
import com.gravitygroup.avangard.domain.orders.OrderDetailsData
import com.gravitygroup.avangard.domain.orders.OrderInfoData
import com.gravitygroup.avangard.presentation.dialogs.order.OrderNotificationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApi {

    @GET("$ORDER_BASE_PATH{$STATUS_ID_KEY}")
    suspend fun ordersByStatus(@Path(STATUS_ID_KEY) statusId: Int): BaseResponseObj<List<OrderDetailsData>>

    @GET("$DETAIL_ORDER_PATH{$INFO_ID_KEY}")
    suspend fun orderById(@Path(INFO_ID_KEY) infoId: Long): BaseResponseObj<OrderInfoData>

    @POST("$DETAIL_ORDER_PATH{$INFO_ID_KEY}")
    suspend fun editOrder(
        @Path(INFO_ID_KEY) infoId: Long,
        @Body request: EditOrderData
    ): BaseResponseObj<EditData>

    @POST("${ORDER_BASE_PATH}{$INFO_ID_KEY}/{$STATUS_ID_KEY}")
    suspend fun setupStatus(
        @Path(INFO_ID_KEY) orderId: Long,
        @Path(STATUS_ID_KEY) statusTypeId: Int
    ): BaseResponseObj<EditData>

    @POST("${NOTIFICATION_ORDER_PATH}{$INFO_ID_KEY}")
    suspend fun setNotify(
        @Path(INFO_ID_KEY) orderId: Long,
        @Body request: OrderNotificationRequest
    ): BaseResponseObj<Any>

    @GET("directory")
    suspend fun getDirectory(): BaseResponseObj<DirectoryData>

    @GET("$ATTACHMENT_PATH{$INFO_ID_KEY}")
    suspend fun getAttachments(
        @Path(INFO_ID_KEY) infoId: Long
    ): BaseResponseObj<List<GetAttachmentData>>

    @POST("$ATTACHMENT_PATH{${INFO_ID_KEY}}")
    suspend fun saveAttachments(
        @Path(INFO_ID_KEY) infoId: Long,
        @Body request: SendAttachmentList
    ): BaseResponseObj<List<GetAttachmentData>>

    @DELETE("$ATTACHMENT_PATH{${INFO_ID_KEY}}")
    suspend fun deleteAttachment(
        @Path(INFO_ID_KEY) infoId: Long,
    ): BaseResponseObj<EditData>

    companion object {

        private const val ORDER_BASE_PATH = "orders/"
        private const val DETAIL_ORDER_PATH = "detailOrder/"

        private const val NOTIFICATION_ORDER_PATH = "notificationOrder/"

        private const val ATTACHMENT_PATH = "attachment/"

        private const val STATUS_ID_KEY = "statusId"

        private const val INFO_ID_KEY = "infoId"
    }
}
