package com.gravitygroup.avangard.presentation.yacht

import com.gravitygroup.avangard.BuildConfig
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.FragmentYachtEnterBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.yacht.EnterYachtFragment.AppType.Captain
import com.gravitygroup.avangard.presentation.yacht.EnterYachtFragment.AppType.Client
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EnterYachtFragment : BaseFragment<YachtFilterVm>(R.layout.fragment_yacht_enter) {

    override val viewModel: YachtFilterVm by sharedViewModel()

    override val stateBinding by lazy { YachtEnterStateBinding() }

    private val vb by viewBinding(FragmentYachtEnterBinding::bind)

    override fun setupViews() {
        when {
            BuildConfig.APP_TYPE == Client.type -> viewModel.navigateToClient()
            BuildConfig.APP_TYPE == Captain.type -> viewModel.navigateToCaptain()
            else -> {}
        }
    }

    sealed class AppType(val type: String) {

        object Client: AppType("client")

        object Captain: AppType("captain")
    }

    inner class YachtEnterStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
            data as YachtListState
        }
    }
}