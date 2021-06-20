package com.gravitygroup.avangard.presentation.dialogs.status

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.R.string
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.DialogExitBinding
import com.gravitygroup.avangard.interactors.orders.data.StatusOrderType
import com.gravitygroup.avangard.presentation.base.BaseDialogFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.dialogs.exit.ConfirmDialogSpecs
import com.gravitygroup.avangard.presentation.dialogs.exit.ConfirmDialogVm
import com.gravitygroup.avangard.presentation.dialogs.status.OrderStatusConfirmType.Accept
import com.gravitygroup.avangard.presentation.dialogs.status.OrderStatusConfirmType.AcceptReject
import com.gravitygroup.avangard.presentation.dialogs.status.OrderStatusConfirmType.Complete
import org.koin.android.viewmodel.ext.android.sharedViewModel

class StatusDialogFragment : BaseDialogFragment<ConfirmDialogVm>(R.layout.dialog_exit) {

    override val viewModel: ConfirmDialogVm by sharedViewModel()
    private val vb by viewBinding(DialogExitBinding::bind)
    override val stateBinding by lazy { ExitStateBinding() }

    private val args: StatusDialogFragmentArgs by navArgs()

    private val orderConfirm by lazy { args.specs }

    override fun setupViews() {
        val specs = when (orderConfirm.confirmType) {
            Accept ->
                ConfirmDialogSpecs(
                    title = requireContext().getString(string.order_info_confirm_accept),
                    confirm = requireContext().getString(string.order_info_accept),
                    deny = requireContext().getString(string.order_info_close),
                )
            AcceptReject ->
                ConfirmDialogSpecs(
                    title = requireContext().getString(string.order_info_confirm_refuse),
                    confirm = requireContext().getString(string.order_info_refuse),
                    deny = requireContext().getString(string.order_info_close),
                )
            Complete -> {
                ConfirmDialogSpecs(
                    title = requireContext().getString(string.order_info_confirm_complete),
                    confirm = requireContext().getString(string.order_info_complete_confirm),
                    deny = requireContext().getString(string.order_info_cancel),
                )
            }
        }

        val statusOrderType = when (orderConfirm.confirmType) {
            Accept -> StatusOrderType.InWork
            AcceptReject -> StatusOrderType.Cancel
            Complete -> StatusOrderType.Complete
        }

        vb?.apply {
            dialogRoot.dialogTitle.text = specs.title
            dialogRoot.dialogConfirm.text = specs.confirm
            dialogRoot.dialogDeny.text = specs.deny

            dialogRoot.dialogConfirm.setSafeOnClickListener {
                viewModel.setupStatus(orderConfirm.orderId, statusOrderType) { viewModel.navigateBack() }
            }
            dialogRoot.dialogDeny.setSafeOnClickListener {
                viewModel.navigateBack()
            }
        }
    }

    inner class ExitStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
        }
    }
}