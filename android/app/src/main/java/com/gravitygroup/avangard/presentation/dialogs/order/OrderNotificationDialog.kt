package com.gravitygroup.avangard.presentation.dialogs.order

import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.DialogOrderNotificationBinding
import com.gravitygroup.avangard.presentation.base.BaseDialogFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel


class OrderNotificationDialog : BaseDialogFragment<OrderNotificationVm>(R.layout.dialog_order_notification) {
    override val viewModel: OrderNotificationVm by sharedViewModel()
    private val vb by viewBinding(DialogOrderNotificationBinding::bind)
    override val stateBinding by lazy { OrderNotificationStateBinding() }

    private val args: OrderNotificationDialogArgs by navArgs()

    private val orderId by lazy { args.specs }
    private val hours: Array<Int> = Array(24) { i -> i }
    private val minutes: Array<Int> = Array(60) { i -> i }

    override fun setupViews() {
        vb?.apply {
            spinnerHour.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, hours)
            spinnerMinute.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, minutes)
            dialogConfirm.setSafeOnClickListener {
                val hour = spinnerHour.selectedItem as Int
                val minute = spinnerMinute.selectedItem as Int
                val comment = etComment.text.toString()
                val time = "%02d:%02d:00".format(hour, minute)
                viewModel.setNotification(orderId, time, comment) { viewModel.navigateBack() }
            }
            dialogDeny.setSafeOnClickListener { viewModel.navigateBack() }
        }
    }

    inner class OrderNotificationStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
        }
    }
}
