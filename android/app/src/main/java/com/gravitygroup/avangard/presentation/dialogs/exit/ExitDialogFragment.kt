package com.gravitygroup.avangard.presentation.dialogs.exit

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.DialogExitBinding
import com.gravitygroup.avangard.presentation.base.BaseDialogFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ExitDialogFragment : BaseDialogFragment<ConfirmDialogVm>(R.layout.dialog_exit) {

    override val viewModel: ConfirmDialogVm by sharedViewModel()
    private val vb by viewBinding(DialogExitBinding::bind)
    override val stateBinding by lazy { ExitStateBinding() }

    override fun setupViews() {
        val specs = ConfirmDialogSpecs(
            title = requireContext().getString(R.string.exit_title),
            confirm = requireContext().getString(R.string.exit_confirm),
            deny = requireContext().getString(R.string.exit_deny),
        )

        vb?.apply {
            dialogRoot.dialogTitle.text = specs.title
            dialogRoot.dialogConfirm.text = specs.confirm
            dialogRoot.dialogDeny.text = specs.deny
            dialogRoot.dialogConfirm.setSafeOnClickListener {
                viewModel.exitFromApp { this@ExitDialogFragment.dismiss() }
            }
            dialogRoot.dialogDeny.setSafeOnClickListener {
                this@ExitDialogFragment.dismiss()
                viewModel.navigateBack()
            }
        }
    }

    inner class ExitStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
        }
    }
}