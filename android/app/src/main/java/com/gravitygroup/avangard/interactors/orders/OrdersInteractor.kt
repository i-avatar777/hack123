package com.gravitygroup.avangard.interactors.orders

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.DAY
import com.gravitygroup.avangard.core.ext.MINUTE
import com.gravitygroup.avangard.core.ext.handleResult
import com.gravitygroup.avangard.core.ext.handleResultWithError
import com.gravitygroup.avangard.core.ext.isCurrentDay
import com.gravitygroup.avangard.core.ext.isNotTimeOrder
import com.gravitygroup.avangard.core.network.base.CacheEntry
import com.gravitygroup.avangard.core.network.base.CachePolicy
import com.gravitygroup.avangard.core.network.base.CachePolicy.Expire
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.domain.image.GetAttachmentData
import com.gravitygroup.avangard.domain.image.SendAttachmentData
import com.gravitygroup.avangard.domain.image.SendAttachmentList
import com.gravitygroup.avangard.domain.orders.EditData
import com.gravitygroup.avangard.domain.orders.GeoPointData
import com.gravitygroup.avangard.domain.orders.OrderDetailsData
import com.gravitygroup.avangard.domain.orders.OrderDetailsData.Companion.toUIModel
import com.gravitygroup.avangard.domain.orders.OrderInfoData
import com.gravitygroup.avangard.domain.orders.OrderInfoData.Companion.toUIModel
import com.gravitygroup.avangard.interactors.orders.data.StatusOrder
import com.gravitygroup.avangard.interactors.orders.data.StatusOrder.OnMaster
import com.gravitygroup.avangard.interactors.orders.data.StatusOrder.Seen
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.Complete
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.InWork
import com.gravitygroup.avangard.interactors.orders.repository.DirectoriesRepository
import com.gravitygroup.avangard.interactors.orders.repository.LocalDataSource
import com.gravitygroup.avangard.interactors.orders.repository.LocalOrdersRepository
import com.gravitygroup.avangard.interactors.orders.repository.MapGeoCodeRepository
import com.gravitygroup.avangard.interactors.orders.repository.RemoteOrdersRepository
import com.gravitygroup.avangard.interactors.orders.repository.ResourceProvider
import com.gravitygroup.avangard.presentation.dialogs.order.OrderNotificationRequest
import com.gravitygroup.avangard.presentation.order_info.CachedOrderUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findName
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel.Companion.findType
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Started
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderUIModel
import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel.Companion.toShort
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder.Companion.toHeader
import com.gravitygroup.avangard.presentation.orders_history.data.HistoryShortOrder.Companion.toHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Date

class OrdersInteractor(
    private val remoteRepository: RemoteOrdersRepository,
    private val localRepo: LocalOrdersRepository,
    private val directoriesRepo: DirectoriesRepository,
    private val mapGeoCodeRepository: MapGeoCodeRepository,
    private val resourceProvider: ResourceProvider,
    private val orderLocalRepo: LocalDataSource<Any, List<OrderShortUIModel>>
) {

    private val _channel = ConflatedBroadcastChannel(false)
    val channel = _channel

    suspend fun getOrderInfo(id: Long): Flow<RequestResult<OrderInfoData>> =
        flow { emit(remoteRepository.orderInfo(id)) }
            .flowOn(Dispatchers.IO)

    suspend fun editOrderInfo(orderFull: OrderFullUIModel): RequestResult<EditData> =
        remoteRepository.editOrder(orderFull)

    suspend fun setupStatus(idOrder: Long, statusOrder: Int): RequestResult<EditData> =
        remoteRepository.setupStatus(idOrder, statusOrder)

    suspend fun setNotify(idOrder: Long, orderNotificationRequest: OrderNotificationRequest): RequestResult<Any> =
        remoteRepository.setNotify(idOrder, orderNotificationRequest)

    suspend fun changeStatusOrder(idOrder: Long, statusOrder: Int) {
        localRepo.cacheStatusShortOrder(idOrder, statusOrder)
        if (localRepo.findFullCachedOrder(idOrder) != null) {
            localRepo.findFullCachedOrder(idOrder)?.let { cachedFullOrder ->
                val modifyCached = cachedFullOrder.orderFullUIModel.copy(
                    status = statusOrder
                )
                changeStartedCachedOrder(modifyCached)
            }
        } else {
            getOrderInfo(idOrder)
                .collect { emitter ->
                    emitter.handleResult { result ->
                        result.data?.also { fullOrder ->
                            val modifyCached = getFullOrderUIModel(idOrder, fullOrder)
                                .copy(
                                    status = statusOrder
                                )
                            changeStartedCachedOrder(modifyCached)
                        }
                    }
                }
        }
    }

    suspend fun changeStartedCachedOrder(fullOrder: OrderFullUIModel) {
        val typeTechUIModel = TypeTechUIModel(
            getDirectories().typeTech.findType(fullOrder.typeTech),
            fullOrder.typeTech
        )
        val nameTechUIModel = NameTechUIModel(
            getDirectories().nameTech.findName(fullOrder.nameTech),
            fullOrder.nameTech
        )
        cachedOrderInfo(
            EditingOrderUIModel(
                Started,
                fullOrder,
                typeTechUIModel,
                nameTechUIModel
            )
        )
    }

    fun cachedOrderInfo(editingOrderUIModel: EditingOrderUIModel) {
        val shortOrder = editingOrderUIModel.orderFullUIModel.toShort(
            editingOrderUIModel.typeTech.techSpec,
            editingOrderUIModel.nameTech.nameSpec
        )
        val cacheOrder = CachedOrderUIModel(
            editingOrderUIModel.orderFullUIModel,
            editingOrderUIModel.typeTech,
            editingOrderUIModel.nameTech
        )
        localRepo.cacheOrderInfo(editingOrderUIModel.orderFullUIModel.id, shortOrder, cacheOrder)
    }

    fun findShortCachedOrder(id: Long): OrderShortUIModel? = localRepo.findShortCachedOrder(id)

    fun findFullCachedOrder(id: Long): CachedOrderUIModel? = localRepo.findFullCachedOrder(id)

    fun cacheEditingOrder(editingOrderUIModel: EditingOrderUIModel) = localRepo.cacheEditingOrder(editingOrderUIModel)

    fun getEditingOrder(): EditingOrderUIModel = localRepo.getEditingOrder()

    fun isEmptyEditing(): Boolean = localRepo.isEmptyEditing()

    suspend fun getRemoteOpenOrders(): List<OrderShortUIModel> {
        return fetchShortOrders(Seen, Expire(ORDER_CACHE_TIME))
            .filter { it.status == StatusOrderType.Open.statusOrder }
    }

    suspend fun getOnMasterOrders(): Flow<List<OrderShortUIModel>> {
        return if (localRepo.getCachedShortOrders().none { shortOrder ->
                shortOrder.status == OnMaster.status
            }) {
            getShortOrders(OnMaster)
                .collect { masterShortList ->
                    if (masterShortList.isNotEmpty()) {
                        localRepo.saveShortOrders(masterShortList)
                    }
                }
            flow { emit(localRepo.getCachedShortOrders()) }
        } else {
            flow { emit(localRepo.getCachedShortOrders()) }
        }
    }

    suspend fun getActualFullOrders(): Flow<List<OrderFullUIModel>> {
        val rawIdOrders = localRepo.getCachedShortOrders().map { it.id }

        val sourceIdOrders = rawIdOrders.filter { id ->
            localRepo.findFullCachedOrder(id) == null
        }
        return if (sourceIdOrders.isNotEmpty()) {
            combine(
                sourceIdOrders.map { id -> getOrderInfo(id) }
            ) { orders ->
                val sourceFullOrders = orders.mapIndexed { index, it ->
                    var fullOrder = OrderFullUIModel.empty()
                    it.handleResult { result ->
                        result.data?.also { infoData ->
                            fullOrder = getFullOrderUIModel(rawIdOrders[index], infoData)
                        }
                    }
                    fullOrder
                }
                val editedFullOrders = localRepo.getCachedFullOrders().map { it.orderFullUIModel }
                sourceFullOrders.plus(editedFullOrders)
            }
        } else {
            flow { emit(localRepo.getCachedFullOrders().map { it.orderFullUIModel }) }
        }
    }

    suspend fun getDayMasterOrders(day: Date) =
        getActualFullOrders()
            .map { list ->
                val filteredOrders = list
                    .filter { order ->
                        val isCurrentDayOrder = order.isCurrentDay(day)
                        val isActualStatus =
                            order.status == InWork.statusOrder || order.status == Complete.statusOrder
                        isActualStatus && (isCurrentDayOrder || order.isNotTimeOrder(day))
                    }
                val masterOrders = toShortActualOrders(filteredOrders)
                masterOrders
            }

    suspend fun getHistoryOrders(): Flow<List<HistoryShortOrder>> =
        getActualFullOrders()
            .map { actualOrders ->
                val filteredOrders = actualOrders
                    .filter { order -> order.status == Complete.statusOrder }
                val completeOrders = toShortActualOrders(filteredOrders)
                val historyItemList = mutableListOf<HistoryShortOrder>()
                if (completeOrders.isNotEmpty()) {
                    completeOrders.first().also { firstOrder ->
                        historyItemList.add(
                            firstOrder.toHeader()
                        )
                    }
                    historyItemList.addAll(completeOrders.map { it.toHistory() })
                } else {
                    historyItemList.addAll(completeOrders.map { it.toHistory() })
                }
                historyItemList
            }

    suspend fun getAttachments(id: Long): Flow<RequestResult<List<GetAttachmentData>>> =
        flow { emit(remoteRepository.getAttachments(id)) }
            .flowOn(Dispatchers.IO)

    suspend fun saveImages(idOrder: Long, queryImages: List<String>) =
        remoteRepository.saveAttachments(
            idOrder,
            SendAttachmentList(
                queryImages
                    .map { SendAttachmentData(it) }
            )
        )

    suspend fun deleteImage(idOrder: Long) =
        remoteRepository.deleteAttachment(idOrder)

    suspend fun setupHasStorageCamPermission(hasStorageCamPermission: Boolean) =
        _channel.send(hasStorageCamPermission)

    suspend fun getDirectories(): DirectoryUIModel =
        directoriesRepo.getDirectories(Expire(DIRECTORY_CACHE_TIME))

    fun getFullOrderUIModel(id: Long, infoData: OrderInfoData) =
        findShortCachedOrder(id)?.let { short ->
            if (short.latitude.isNotEmpty() && short.longitude.isNotEmpty()) {
                infoData.toUIModel(id, short.latitude, short.longitude)
            } else {
                infoData.toUIModel(id)
            }
        } ?: infoData.toUIModel(id)

    private suspend fun fetchShortOrders(status: StatusOrder, cachePolicy: CachePolicy): List<OrderShortUIModel> =
        when (cachePolicy) {
            is Expire -> {
                if (orderLocalRepo.has(status.key) &&
                    orderLocalRepo.get(status.key)!!.createdAt + cachePolicy.expires > System.currentTimeMillis()
                ) {
                    orderLocalRepo.get(status.key)!!.value
                } else {
                    fetchAndCache(status)
                }
            }
            else -> fetchAndCache(status)
        }

    private suspend fun fetchAndCache(status: StatusOrder): List<OrderShortUIModel> {
        val list = fetchShortOrders(status)
        list
            .takeIf { it.isNotEmpty() }
            ?.let { orderLocalRepo.set(status.key, CacheEntry(key = status.key, value = it)) }

        return orderLocalRepo.get(status.key)?.value ?: emptyList()
    }

    private suspend fun getShortOrders(statusOrder: StatusOrder): Flow<List<OrderShortUIModel>> {
        return flow { emit(fetchShortOrders(statusOrder)) }
    }

    private suspend fun fetchShortOrders(statusOrder: StatusOrder): List<OrderShortUIModel> {
        var listShortOrders: List<OrderShortUIModel>? = null
        getOrdersByStatus(statusOrder)
            .map { orders ->
                var ordersList = emptyList<OrderDetailsData>()
                val directories = getDirectories()
                var listGeoPoints = listOf<GeoPointData>()
                orders.handleResultWithError({ result ->
                    result.data?.also {
                        ordersList = it
                        listGeoPoints = getGeoPoints(ordersList)
                    }
                }, {
                    Timber.e("getOrdersOnSeenError: $it")
                })

                val ordersShortUIModel = ordersList.map { data ->
                    val typeTech = directories.typeTech.findType(data.typeTech)
                    val nameTech = directories.nameTech.findName(data.nameTech)
                    listGeoPoints
                        .find { geoPointData -> data.id == geoPointData.id }
                        ?.geoPoint
                        ?.let { (latitude, longitude) ->
                            data.toUIModel(typeTech, nameTech, latitude, longitude)
                        } ?: data.toUIModel(typeTech, nameTech)
                }
                ordersShortUIModel
            }
            .collect {
                listShortOrders = it
            }
        return listShortOrders ?: emptyList()
    }

    private suspend fun getGeoPoints(ordersList: List<OrderDetailsData>): List<GeoPointData> {
        var listGeoPoints = listOf<GeoPointData>()
        val sourceAddressOrders = ordersList
            .map { order -> order.id to order.addressName }
            .filter { (_, address) ->
                address.isNotEmpty() && address != "-"
            }
            .map { (id, address) ->
                val cityName = resourceProvider.getString(R.string.base_city_name)
                val actualAddress = if (address.contains(cityName)) {
                    address
                } else {
                    "$cityName $address"
                }
                id to actualAddress
            }
        val remoteRequests = sourceAddressOrders
            .map { addressPair -> mapGeoCodeRepository.getGeoPoint(addressPair, CachePolicy.Always) }
        remoteRequests
            .forEach { remote ->
                remote.collect { geoPoint ->
                    listGeoPoints = listGeoPoints.plus(geoPoint)
                }
            }
        return listGeoPoints
    }

    private suspend fun getOrdersByStatus(statusOrder: StatusOrder): Flow<RequestResult<List<OrderDetailsData>>> =
        flow { emit(remoteRepository.ordersByStatus(statusOrder)) }
            .flowOn(Dispatchers.IO)

    private suspend fun toShortActualOrders(fullActualOrders: List<OrderFullUIModel>): List<OrderShortUIModel> =
        fullActualOrders.map { fullOrder ->
            fullOrder.toShort(
                getDirectories().typeTech.findType(fullOrder.typeTech),
                getDirectories().nameTech.findName(fullOrder.nameTech)
            )
        }

    companion object {

        private const val DIRECTORY_CACHE_TIME = DAY
        private const val ORDER_CACHE_TIME = 5 * MINUTE
    }
}

