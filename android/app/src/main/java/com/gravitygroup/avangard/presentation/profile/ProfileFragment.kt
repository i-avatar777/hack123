package com.gravitygroup.avangard.presentation.profile

import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.FragmentProfileBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.profile.data.ProfileUIModel
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<ProfileVm>(R.layout.fragment_profile) {

    override val viewModel: ProfileVm by viewModel()

    private val vb by viewBinding(FragmentProfileBinding::bind)

    override val stateBinding by lazy { ProfileStateBinding() }

    private val args: ProfileFragmentArgs by navArgs()

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
        }
        viewModel.getProfileData()
    }

    inner class ProfileStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        private var profileData by RenderProp(ProfileUIModel.empty()) { profileData ->
            vb?.apply {
                tvLogin.text = profileData.login ?: ProfileUIModel.STRING_DEFAULT
                tvFio.text = profileData.fio ?: ProfileUIModel.STRING_DEFAULT
                Glide.with(ivAvatar)
                    .load(profileData.image)
                    .centerCrop()
                    .placeholder(R.drawable.photo_background)
            }
        }

        override fun bind(data: IViewModelState) {
            data as ProfileState
            isLoading = data.isLoading
            profileData = data.profileData
        }
    }

}
