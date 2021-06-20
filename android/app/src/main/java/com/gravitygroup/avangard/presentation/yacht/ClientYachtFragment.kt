package com.gravitygroup.avangard.presentation.yacht

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentYachtClientEnterBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ClientYachtFragment : BaseFragment<YachtFilterVm>(R.layout.fragment_yacht_client_enter) {

    override val viewModel: YachtFilterVm by sharedViewModel()

    override val stateBinding by lazy { YachtEnterStateBinding() }

    private val vb by viewBinding(FragmentYachtClientEnterBinding::bind)

    override fun setupViews() {
        vb?.apply {
            searchYacht.setSafeOnClickListener {
                viewModel.navigateToFilter()
            }
        }
    }

    inner class YachtEnterStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
            data as YachtListState
        }
    }
}