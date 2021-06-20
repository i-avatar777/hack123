package com.gravitygroup.avangard.presentation.order_info

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.NestedMainDirections
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.utils.SingleLiveEvent
import com.gravitygroup.avangard.domain.image.GetAttachmentData.Companion.toUIModel
import com.gravitygroup.avangard.domain.image.ImageUIModel
import com.gravitygroup.avangard.interactors.file.FileManagerInteractor
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.dialogs.status.OrderConfirmUIModel
import com.gravitygroup.avangard.presentation.dialogs.status.OrderStatusConfirmType
import com.gravitygroup.avangard.presentation.order_info.data.EditingOrderType.Saved
import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel
import com.gravitygroup.avangard.presentation.photo.PhotoSpecs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

class OrderInfoVm(
    private val context: Context,
    private val ordersInteractor: OrdersInteractor,
    private val fileManagerInteractor: FileManagerInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<OrderInfoState>(handleState, OrderInfoState()) {

    private val _showCamStoragePermissionRequest = MutableLiveData<Boolean>()
    val showCamStoragePermissionRequest: LiveData<Boolean> = _showCamStoragePermissionRequest

    private val _showOpenAddImageDialog = SingleLiveEvent<Int>()
    val showOpenAddImageDialog: LiveData<Int> = _showOpenAddImageDialog

    fun onAttachImage(orderId: Long, idToUriList: List<Pair<String, Uri>>) {
        updateState {
            it.copy(isLoading = true)
        }
        launch {
            // сервер возвращает только idPhoto, его нужно связать с добавленным base64, поэтому
            // конвертим / сохраняем / отображаем добавленные по одной
            idToUriList.forEach { idToUri ->
                val uri = idToUri.second
                val base64Image = fileManagerInteractor.encodeToToBase64(context, uri)
                ordersInteractor.saveImages(orderId, listOf(base64Image))
                    .handleResultWithError(
                        { result ->
                            result.data?.also { attachments ->
                                val attachment = attachments.firstOrNull()
                                attachment?.let {
                                    updateState {
                                        it.copy(
                                            attachmentImages = currentState.attachmentImages
                                                + ImageUIModel(
                                                    attachment.idPhoto,
                                                    pathEncoded = base64Image
                                                )
                                        )
                                    }
                                    Timber.d("saveImagesSuccess idPhoto: ${attachment.idPhoto}")
                                }
                            }
                        },
                        {
                            mapAndShowError(it)
                        }
                    )
            }
            updateStateInMain { state ->
                state.copy(isLoading = false)
            }
        }
    }

    fun removeImage(image: ImageUIModel) {
        notify(
            Notify.DialogAction(
                message = context.getString(R.string.order_info_remove_image_confirm_message),
                positiveActionLabel = context.getString(R.string.order_info_remove_image_confirm_yes),
                positiveActionHandler = {
                    doRemoveImage(image)
                },
                negativeActionLabel = context.getString(R.string.order_info_remove_image_confirm_no)
            )
        )
    }

    private fun doRemoveImage(image: ImageUIModel) {
        updateState { it.copy(isLoading = true) }
        launch {
            ordersInteractor.deleteImage(image.id.toLong())
                .handleResultWithError({ _ ->
                    updateState {
                        it.copy(attachmentImages = currentState.attachmentImages.filter { it != image })
                    }
                }, {
                    Timber.d("deleteImageError$it")
                })
            updateStateInMain { it.copy(isLoading = false) }
        }
    }

    fun navigateToPhotoScreen(image: ImageUIModel) {
        val index = currentState.attachmentImages.indexOf(image)
        if (index < 0) {
            return
        }
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToPhoto(
                    PhotoSpecs(
                        context.getString(R.string.photo_full_title, index + 1),
                        image.pathEncoded,
                        image.uri
                    )
                )
            )
        )
    }

    fun navigateToOrderEditScreen() {
        state.value?.also { state ->
            navigate(
                NavigationCommand.Dir(
                    NestedMainDirections.actionToOrderEdit(state.orderFull)
                )
            )
        }
    }

    fun navigateToAcceptOrder(orderId: Long, confirmType: OrderStatusConfirmType) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToStatusConfirm(
                    OrderConfirmUIModel(orderId, confirmType)
                )
            )
        )
    }

    fun navigateToNotifyOrder(orderId: Long) {
        navigate(
            NavigationCommand.Dir(
                NestedMainDirections.actionToOrderNotification(
                    orderId
                )
            )
        )
    }

    fun requestOrderInfo(id: Long, nameTech: String, typeTech: String) {
        val hasCashedInfo = ordersInteractor.findFullCachedOrder(id) != null
        val startupState = currentState.attachmentImages.isEmpty()
        when {
             hasCashedInfo -> {
                ordersInteractor.findFullCachedOrder(id)?.also { cachedFullOrder ->
                    updateState {
                        it.copy(
                            orderFull = cachedFullOrder.orderFullUIModel,
                            nameTech = cachedFullOrder.nameTech,
                            typeTech = cachedFullOrder.typeTech
                        )
                    }
                }
                launch(Dispatchers.Main) {
                    ordersInteractor.getAttachments(id)
                        .collect { attachment ->
                            attachment.handleResult { result ->
                                val attachmentList = result.data?.let { list ->
                                    list.map { it.toUIModel() }
                                }
                                attachmentList?.also { attachments ->
                                    updateState {
                                        it.copy(
                                            attachmentImages = attachments
                                        )
                                    }
                                }
                            }
                        }
                }
            }
            startupState -> {
                updateState { it.copy(isLoading = true) }
                launch(Dispatchers.Main) {
                    ordersInteractor.getOrderInfo(id)
                        .combine(ordersInteractor.getAttachments(id))
                        { orderFull, attachment ->
                            var order = OrderFullUIModel.empty()
                            var attachmentList = listOf<ImageUIModel>()
                            orderFull.handleResult { result ->
                                result.data?.also { infoData ->
                                    order = ordersInteractor.getFullOrderUIModel(id, infoData)
                                }
                            }
                            attachment.handleResult { result ->
                                result.data?.also { list ->
                                    attachmentList = list.map { it.toUIModel() }
                                }
                            }
                            order to attachmentList
                        }
                        .collect { (order, attachments) ->
                            ordersInteractor.changeStartedCachedOrder(order)
                            updateState {
                                it.copy(
                                    orderFull = order,
                                    nameTech = NameTechUIModel(
                                        nameTech,
                                        order.nameTech
                                    ),
                                    typeTech = TypeTechUIModel(
                                        typeTech,
                                        order.typeTech
                                    ),
                                    attachmentImages = attachments
                                )
                            }
                        }
                    updateStateInMain { it.copy(isLoading = false) }
                }
            }
            ordersInteractor.getEditingOrder().editingOrderType is Saved -> {
                updateState {
                    it.copy(
                        orderFull = ordersInteractor.getEditingOrder().orderFullUIModel,
                        nameTech = ordersInteractor.getEditingOrder().nameTech,
                        typeTech = ordersInteractor.getEditingOrder().typeTech
                    )
                }
            }
        }
    }

    fun setupHasStorageCamPermission(hasStorageCamPermission: Boolean) =
        launch(Dispatchers.Main) {
            ordersInteractor.setupHasStorageCamPermission(hasStorageCamPermission)
        }

    fun observeHasStorageCamPermission() {
        launch(Dispatchers.Main) {
            ordersInteractor.channel
                .asFlow()
                .collect {
                    _showCamStoragePermissionRequest.value = it
                }
        }
    }

    fun checkOpenAddImage() {
        val currentSize = currentState.attachmentImages.size
        when {
            currentSize >= LIMIT_ADD_IMAGES -> {
                notify(Notify.DialogAction(
                    title = context.getString(R.string.order_info_photo_exceed_limit_title),
                    message = context.getString(R.string.order_info_photo_exceed_limit_body),
                    positiveActionLabel = context.getString(android.R.string.ok),
                    positiveActionHandler = {}
                ))
            }
            else -> {
                _showOpenAddImageDialog.value = LIMIT_ADD_IMAGES - currentSize
            }
        }
    }

    companion object {

        private const val LIMIT_ADD_IMAGES = 5
        private const val DEFAULT_SIZE_IMAGES = 0
    }
}

data class OrderInfoState(
    val isLoading: Boolean = false,
    val orderFull: OrderFullUIModel = OrderFullUIModel.empty(),
    val typeTech: TypeTechUIModel = TypeTechUIModel.empty(),
    val nameTech: NameTechUIModel = NameTechUIModel.empty(),
    var attachmentImages: List<ImageUIModel> = listOf()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }

}
