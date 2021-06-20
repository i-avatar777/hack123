package com.gravitygroup.avangard.presentation.auth

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PreviewAuthFragment : BaseFragment<AuthVm>(R.layout.activity_splash) {

    override val viewModel: AuthVm by sharedViewModel()

    override fun setupViews() {
        viewModel.isYachtAuthorized()
    }
}