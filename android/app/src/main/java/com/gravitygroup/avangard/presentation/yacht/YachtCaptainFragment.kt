package com.gravitygroup.avangard.presentation.yacht

import android.os.Bundle
import android.view.View
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.FragmentCaptainCabinetBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Captain
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import org.koin.android.viewmodel.ext.android.sharedViewModel

class YachtCaptainFragment : BaseFragment<YachtFilterVm>(R.layout.fragment_captain_cabinet) {

    override val viewModel: YachtFilterVm by sharedViewModel()

    override val stateBinding by lazy { YachtCaptainStateBinding() }

    private val vb by viewBinding(FragmentCaptainCabinetBinding::bind)

    override fun setupViews() {
        vb?.apply {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.socketMessageObserve.observe(viewLifecycleOwner, {
            when (it) {
                Captain -> {
                    val sheet = DemoBottomSheetFragment()
                    sheet.show(parentFragmentManager, "DemoBottomSheetFragment")
                }
                else -> {}
            }
        })
        viewModel.initSocket()
    }

    inner class YachtCaptainStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
            data as YachtListState
        }
    }
}