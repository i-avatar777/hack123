package com.gravitygroup.avangard.presentation.menu.settings

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.recycler.BaseDelegationAdapter
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentSettingsBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.menu.settings.SettingsVm.SettingViewState
import com.gravitygroup.avangard.presentation.menu.settings.adapter.TimeNotificationAdapterDelegates
import com.gravitygroup.avangard.presentation.menu.settings.data.TimeNotificationUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<SettingsVm>(R.layout.fragment_settings) {

    override val viewModel: SettingsVm by viewModel()

    override val stateBinding by lazy { SettingsStateBinding() }

    private val vb by viewBinding(FragmentSettingsBinding::bind)

    private val adapter by lazy { createAdapter() }

    override fun setupViews() {
        vb?.apply {
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            icCheck.setSafeOnClickListener {
                viewModel.setupTimeNotification()
            }
            rvTimesNotification.apply {
                val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                layoutManager = manager
                adapter = this@SettingsFragment.adapter
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainWhiteBackgroundColor))
            }

        }
        viewModel.requestPositionTimeNotification()
    }

    private fun createAdapter() = BaseDelegationAdapter(
        TimeNotificationAdapterDelegates.timeNotificationDelegate(
            ::changeTime
        )
    )

    private fun changeTime(idTimeNotification: Int) {
        viewModel.changeTimeNotification(idTimeNotification)
    }

    inner class SettingsStateBinding : StateBinding() {

        private var enabledPositionProp by RenderProp(listOf<TimeNotificationUIModel>()) { timesNotification ->
            adapter.items = timesNotification
            adapter.notifyDataSetChanged()
        }

        override fun bind(data: IViewModelState) {
            data as SettingViewState
            enabledPositionProp = data.timesUIModelList
        }
    }
}