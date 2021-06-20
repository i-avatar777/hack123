package com.gravitygroup.avangard.presentation.menu.about

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class AboutAppFragment : BaseFragment<AboutAppVm>(R.layout.fragment_about_app) {

    override val viewModel: AboutAppVm by viewModel()

//    private val vb by viewBinding(FragmentAboutAppBinding::bind)

    override fun setupViews() {
//        vb?.apply {
//            toolbar.setNavigationOnClickListener {
//                viewModel.navigateBack()
//            }
//            tvCreatorApp.movementMethod = LinkMovementMethod.getInstance()
//        }
    }
}