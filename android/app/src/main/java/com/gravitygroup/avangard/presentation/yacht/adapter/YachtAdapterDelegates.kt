package com.gravitygroup.avangard.presentation.yacht.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gravitygroup.avangard.core.ext.dpToPx
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemYachtBinding
import com.gravitygroup.avangard.interactors.yacht.data.YachtUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnYachtItemClick = (YachtUIModel) -> Unit

object YachtAdapterDelegates {

    fun yachtListDelegate(onOrderItemClick: OnYachtItemClick) =
        adapterDelegateViewBinding<YachtUIModel, RecyclerViewItem, ItemYachtBinding>(
            { layoutInflater, root -> ItemYachtBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16.dpToPx))
                    Glide
                        .with(this.ivImage.context)
                        .load(item.image)
                        .apply(requestOptions)
                        .into(ivImage)
                    val caption = if (item.id == 1) {
                        "Aston Marine 35"
                    } else {
                        "Loundge Marine 450"
                    }

                    orderTitle.text = caption
                    tvPriceItem.text = item.price.toString()
                    ratingBarIndicator.rating = item.rating.toFloat()

                    requestOrder.setSafeOnClickListener { onOrderItemClick.invoke(item)}
                }
            }
        }
}
