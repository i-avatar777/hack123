package com.gravitygroup.avangard.presentation.order_info.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.IpCons
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.DialogAddImageBinding
import com.gravitygroup.avangard.presentation.order_info.data.AddImageResult

class AddImageDialog : BottomSheetDialogFragment() {

    private val vb by viewBinding(DialogAddImageBinding::bind)

    companion object {

        const val TAG = "AddImageDialog"
        private const val ARG_REQUEST_KEY = "arg_request_key"
        private const val ARG_RESULT = "arg_result"
        private const val ARG_LEFT_QUANTITY = "arg_left_quantity"

        private const val STRING_DEFAULT = ""
        private const val LIMIT_PHOTO_DEFAULT = 5

        fun getInstance(
            requestKey: String,
            leftQuantity: Int
        ): AddImageDialog =
            AddImageDialog().apply {
                arguments = bundleOf(
                    ARG_REQUEST_KEY to requestKey,
                    ARG_LEFT_QUANTITY to leftQuantity
                )
            }

        fun extractResult(result: Bundle): AddImageResult =
            if (result.containsKey(ARG_RESULT)) {
                result.getSerializable(ARG_RESULT) as AddImageResult
            } else {
                AddImageResult.None
            }
    }

    private val requestKey: String by lazy {
        requireArguments().getString(ARG_REQUEST_KEY, STRING_DEFAULT)
    }

    private val leftPhotoQuantity: Int by lazy {
        requireArguments().getInt(ARG_LEFT_QUANTITY, LIMIT_PHOTO_DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dialog_add_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
    }

    private fun initListeners() {
        vb?.apply {
            llCamera.setOnClickListener {
                ImagePicker.cameraOnly().start(this@AddImageDialog)
            }
            llGallery.setOnClickListener {
                ImagePicker.create(this@AddImageDialog).returnMode(ReturnMode.NONE)
                    .folderMode(true)
                    .toolbarFolderTitle(getString(R.string.order_info_photo))
                    .includeVideo(false)
                    .multi()
                    .limit(leftPhotoQuantity)
                    .enableLog(false)
                    .start()
            }
            llCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (shouldHandle(requestCode, data)) {
            val images = ImagePicker.getImages(data)
            val dataList = mutableListOf<Pair<String, Uri>>()
            images.forEach {
                it?.also { image ->
                    dataList += image.path to image.uri
                }
            }
            deliverResult(AddImageResult.FileSelected(dataList))
            dismiss()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun shouldHandle(requestCode: Int, data: Intent?) =
        requestCode == IpCons.RC_IMAGE_PICKER && data != null

    private fun deliverResult(result: AddImageResult) {
        parentFragmentManager
            .setFragmentResult(
                requestKey,
                bundleOf(
                    ARG_RESULT to result
                )
            )
        dismiss()
    }
}
