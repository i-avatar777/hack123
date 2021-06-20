package com.gravitygroup.avangard.core.ext

import android.text.SpannableString
import android.text.style.UnderlineSpan
import com.gravitygroup.avangard.core.utils.DateUtils.toFullDateString
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemAddressOrderContentBinding
import com.gravitygroup.avangard.databinding.ItemOrderBinding
import com.gravitygroup.avangard.presentation.auth.AuthFragment.Companion.callToSupport
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel

fun ItemOrderBinding.renderState(orderInfo: OrderShortUIModel) {
    addressNameTv.text = orderInfo.addressName
    val telUnderline = SpannableString(orderInfo.tel)
    telUnderline.setSpan(UnderlineSpan(), 0, telUnderline.length, 0)
    tvPhone.text = telUnderline
    tvPhone.setSafeOnClickListener { this.rootContainer.context.callToSupport(telUnderline.toString()) }
    tvType.text = orderInfo.typeTech
    tvName.text = orderInfo.nameTech
    tvNumber.text = orderInfo.num
    tvDate.text = orderInfo.dateTimeCreate.toFullDateString()
}

fun ItemAddressOrderContentBinding.renderState(orderInfo: OrderShortUIModel) {
    addressNameTv.text = orderInfo.addressName
    val telUnderline = SpannableString(orderInfo.tel)
    telUnderline.setSpan(UnderlineSpan(), 0, telUnderline.length, 0)
    tvPhone.text = telUnderline
    tvPhone.setSafeOnClickListener { this.rootContainer.context.callToSupport(telUnderline.toString()) }
    tvType.text = orderInfo.typeTech
    tvName.text = orderInfo.nameTech
    tvNumber.text = orderInfo.num
    tvDate.text = orderInfo.dateTimeCreate.toFullDateString()
}