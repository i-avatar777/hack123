package com.gravitygroup.avangard.presentation.order_info

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.PermissionUtils.STORAGE_CAM_PERMISSION_REQUEST
import com.gravitygroup.avangard.core.utils.PermissionUtils.checkStorageCamPermissions
import com.gravitygroup.avangard.core.utils.PermissionUtils.hasStorageCamPermission
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentOrderInfoBinding
import com.gravitygroup.avangard.domain.image.ImageUIModel
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.InWork
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType.Open
import com.gravitygroup.avangard.presentation.auth.AuthFragment.Companion.callToSupport
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.dialogs.exit.ConfirmDialogVm
import com.gravitygroup.avangard.presentation.dialogs.status.OrderStatusConfirmType.*
import com.gravitygroup.avangard.presentation.order_info.adapter.OrderInfoPhotoAdapterDelegate
import com.gravitygroup.avangard.presentation.order_info.adapter.OrderSeeDateAdapterDelegates
import com.gravitygroup.avangard.presentation.order_info.data.*
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel.Companion.isEmpty
import com.gravitygroup.avangard.presentation.order_info.dialog.AddImageDialog
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.gravitygroup.avangard.view.loading.LoadingState
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class OrderInfoFragment : BaseFragment<OrderInfoVm>(R.layout.fragment_order_info) {

    private val args: OrderInfoFragmentArgs by navArgs()

    override val viewModel: OrderInfoVm by viewModel()

    private val confirmViewModel: ConfirmDialogVm by sharedViewModel()

    private val vb by viewBinding(FragmentOrderInfoBinding::bind)

    override val stateBinding by lazy { OrdersStateBinding() }

    private val imagesAdapter by lazy { createImagesAdapter() }

    private val dateTimeExecAdapter by lazy { createSeeDateExecAdapter() }

    private val order by lazy { args.specs }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmViewModel.statusOrderTypeLiveData.observe(viewLifecycleOwner, { statusType ->
            vb?.let { renderStatusState(statusType, it) }
        })

        viewModel.requestOrderInfo(order.id, order.nameTech, order.typeTech)
        confirmViewModel.requestStatus(order.id)

        viewModel.observeHasStorageCamPermission()
        viewModel.showCamStoragePermissionRequest.observe(viewLifecycleOwner, { hasPermissions ->
            if (hasPermissions) {
                viewModel.setupHasStorageCamPermission(false)
                viewModel.checkOpenAddImage()
            }
        })
        viewModel.showOpenAddImageDialog.observe(viewLifecycleOwner, { leftQuantity ->
            openAddImageDialog(leftQuantity)
        })
    }

    override fun setupViews() {
        registerFragmentResult(REQUEST_SELECT_IMAGE, this.childFragmentManager)
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            ivAddPhoto.setSafeOnClickListener {
                if (requireActivity().hasStorageCamPermission()) {
                    viewModel.checkOpenAddImage()
                } else {
                    requireActivity().checkStorageCamPermissions()
                }
            }
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            ivEdit.setSafeOnClickListener {
                viewModel.navigateToOrderEditScreen()
            }

            ivClock.setSafeOnClickListener {
                viewModel.navigateToNotifyOrder(order.id)
            }

            recyclerPhoto.apply {
                val manager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                layoutManager = manager
                adapter = this@OrderInfoFragment.imagesAdapter
            }

            rvDateTimeExec.apply {
                val manager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                layoutManager = manager
                adapter = this@OrderInfoFragment.dateTimeExecAdapter
            }
        }
    }

    override fun onFragmentResult(key: String, result: Bundle) {
        when (key) {
            REQUEST_SELECT_IMAGE -> onResultSelectImage(result)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_CAM_PERMISSION_REQUEST -> viewModel.setupHasStorageCamPermission(requireActivity().hasStorageCamPermission())
        }
    }

    private fun openAddImageDialog(leftPhotoQuantity: Int) =
        AddImageDialog.getInstance(REQUEST_SELECT_IMAGE, leftPhotoQuantity)
            .show(childFragmentManager, AddImageDialog.TAG)

    private fun renderStatusState(statusOrderType: StatusOrderType, vb: FragmentOrderInfoBinding) {
        when (statusOrderType) {
            Open -> renderOpenState(vb)
            InWork -> renderWorkState(vb)
            StatusOrderType.Complete -> renderCompleteState(vb)
            else -> {
            }
        }
    }

    private fun renderOpenState(vb: FragmentOrderInfoBinding) {
        vb.apply {
            acceptRoot.isVisible = true
            completeRoot.isVisible = false
            buttonAccept.setSafeOnClickListener {
                viewModel.navigateToAcceptOrder(order.id, Accept)
            }

            buttonReject.setSafeOnClickListener {
                viewModel.navigateToAcceptOrder(order.id, AcceptReject)
            }
        }
    }

    private fun renderWorkState(vb: FragmentOrderInfoBinding) {
        vb.apply {
            completeRoot.isVisible = true
            acceptRoot.isVisible = false
            buttonComplete.setSafeOnClickListener {
                viewModel.navigateToAcceptOrder(order.id, Complete)
            }
        }
    }

    private fun disabledIcon(imageView: ImageView) {
        imageView.isEnabled = false
        imageView.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.mainButtonDisable
            ), android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun renderCompleteState(vb: FragmentOrderInfoBinding) {
        vb.apply {
            disabledIcon(ivClock)
            disabledIcon(ivEdit)
            buttonComplete.isEnabled = false
            buttonComplete.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.mainButtonDisable)
        }
    }

    private fun onResultSelectImage(result: Bundle) {
        when (val data = AddImageDialog.extractResult(result)) {
            is AddImageResult.FileSelected -> viewModel.onAttachImage(order.id, data.data)
        }
    }

    private fun showImage(image: ImageUIModel) {
        viewModel.navigateToPhotoScreen(image)
    }

    private fun removeImage(image: ImageUIModel) {
        viewModel.removeImage(image)
    }

    private fun renderImages(list: List<ImageUIModel>) {
        imagesAdapter.items = list
    }

    private fun addDateTimeExec(list: List<DateTimeExecUIModel>) {
        dateTimeExecAdapter.items = list
        dateTimeExecAdapter.notifyDataSetChanged()
    }

    companion object {

        const val REQUEST_SELECT_IMAGE = "request_select_image"
    }

    inner class OrdersStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            vb?.loading?.apply {
                render(
                    if (it) LoadingState.Transparent
                    else LoadingState.None
                )
            }
        }

        private var shortOrderProp by RenderProp(OrderShortUIModel.empty()) { order ->
            vb?.apply {
                tvTitle.text = order.num
                tvAddress.text = order.addressName
                tvPhone.text = order.tel
                tvPhone.setSafeOnClickListener { requireContext().callToSupport(order.tel) }
                tvType.text = order.typeTech
                tvName.text = order.nameTech
            }
        }

        private var fullOrderProp by RenderProp(OrderFullUIModel.empty()) { order ->
            vb?.apply {
                tvTitle.text = order.num
                tvAddress.text = order.addressName
                tvPhone.text = order.tel
                tvPhone.setSafeOnClickListener { requireContext().callToSupport(order.tel) }

                tvDescriptionStep.text = order.stage
                tvDescription.text = order.equipment
                tvMalfunction.text = order.defect
            }
        }

        private var typeTechProp by RenderProp(TypeTechUIModel.empty()) {
            vb?.tvType?.text = it.techSpec
        }

        private var nameTechProp by RenderProp(NameTechUIModel.empty()) {
            vb?.tvName?.text = it.nameSpec
        }

        override fun bind(data: IViewModelState) {
            data as OrderInfoState
            if (data.orderFull.isEmpty()) {
                shortOrderProp = order
            }
            isLoading = data.isLoading
            fullOrderProp = data.orderFull
            typeTechProp = data.typeTech
            nameTechProp = data.nameTech
            renderImages(data.attachmentImages)
            addDateTimeExec(data.orderFull.dateTimeExec)
        }
    }

    private fun createImagesAdapter() = BaseDelegationAdapter(
        OrderInfoPhotoAdapterDelegate.orderInfoPhoto(this::removeImage, this::showImage)
    )

    private fun createSeeDateExecAdapter() = BaseDelegationAdapter(
        OrderSeeDateAdapterDelegates.orderSeeDateDelegate()
    )
}
