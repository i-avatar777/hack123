package com.gravitygroup.avangard.presentation.order_info.adapter

import android.net.Uri
import com.gravitygroup.avangard.core.ext.glide.networkLoadImage
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemPhotoBinding
import com.gravitygroup.avangard.domain.image.ImageUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnRemoveClick = (ImageUIModel) -> Unit
typealias OnImageClick = (ImageUIModel) -> Unit

object OrderInfoPhotoAdapterDelegate {

    fun orderInfoPhoto(clickRemoveImage: OnRemoveClick, clickShowImage: OnImageClick) =
        adapterDelegateViewBinding<ImageUIModel, RecyclerViewItem, ItemPhotoBinding>(
            { layoutInflater, root -> ItemPhotoBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    ivImage.networkLoadImage(item.pathEncoded)
                    item.uri.takeIf { it != Uri.EMPTY }?.also { file ->
                        ivImage.setImageURI(file)
                    }
                    ivImage.setSafeOnClickListener { clickShowImage.invoke(item) }
                    ivRemove.setSafeOnClickListener { clickRemoveImage.invoke(item) }
                }
            }
        }
}
