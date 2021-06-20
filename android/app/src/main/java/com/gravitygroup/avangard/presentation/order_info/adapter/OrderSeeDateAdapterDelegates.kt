package com.gravitygroup.avangard.presentation.order_info.adapter

import android.annotation.SuppressLint
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.DateUtils.timeExecToString
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.databinding.ItemOrderSeeDateBinding
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object OrderSeeDateAdapterDelegates {
    @SuppressLint("SetTextI18n")
    fun orderSeeDateDelegate() =
            adapterDelegateViewBinding<DateTimeExecUIModel, RecyclerViewItem, ItemOrderSeeDateBinding>(
                    { layoutInflater, root -> ItemOrderSeeDateBinding.inflate(layoutInflater, root, false) }
            ) {
                bind {
                    binding.apply {
                        tvDate.text = item.start.toSimpleDateString()
                        tvTime.text = timeExecToString(item)

                        if (item.index > FIRST_POSITION) {
                            titleDateSee.setText(R.string.order_info_date_more)
                            titleTimeSee.setText(R.string.order_info_time_more)
                        }
                    }
                }
            }

    private const val FIRST_POSITION = 0

}
