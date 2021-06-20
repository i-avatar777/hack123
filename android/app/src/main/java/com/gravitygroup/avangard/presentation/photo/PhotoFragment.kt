package com.gravitygroup.avangard.presentation.photo

import android.net.Uri
import androidx.navigation.fragment.navArgs
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.glide.networkLoadImage
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.FragmentPhotoBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import org.koin.android.viewmodel.ext.android.viewModel

class PhotoFragment : BaseFragment<PhotoVm>(R.layout.fragment_photo) {

    private val args: PhotoFragmentArgs by navArgs()

    override val viewModel: PhotoVm by viewModel()

    private val vb by viewBinding(FragmentPhotoBinding::bind)

    override val stateBinding by lazy { PhotoStateBinding() }

    override fun setupViews() {
        vb?.apply {
            setupMainToolbar(toolbar)
            toolbar.setNavigationOnClickListener {
                viewModel.navigateBack()
            }
            args.specs?.let {
                tvTitle.text = it.title
                ivPhoto.networkLoadImage(it.pathEncoded)
                if (it.uri != Uri.EMPTY) {
                    ivPhoto.setImageURI(it.uri)
                }
            }
        }
    }

    inner class PhotoStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) {
            // show/hide progress
        }

        override fun bind(data: IViewModelState) {
            data as PhotoState
            isLoading = data.isLoading
        }
    }
}

