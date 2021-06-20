package com.gravitygroup.avangard.presentation.order_edit.adapter

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.core.utils.DateUtils.timeExecToString
import com.gravitygroup.avangard.core.utils.DateUtils.toSimpleDateString
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.ItemOrderEditDateBinding
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel.Companion.LONG_DEFAULT
import com.gravitygroup.avangard.presentation.order_info.data.DateTimeExecUIModel.Companion.isNotEmpty
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

typealias OnDateClear = (DateTimeExecUIModel, Int) -> Unit
typealias OnDateFieldClick = (DateTimeExecUIModel, Int) -> Unit
typealias OnTimeFieldClick = (DateTimeExecUIModel, Int) -> Unit

object OrderEditDateAdapterDelegates {

    fun orderEditDateDelegate(
        onDateClear: OnDateClear,
        onDateFieldClick: OnDateFieldClick,
        onTimeFieldClick: OnTimeFieldClick
    ) =
        adapterDelegateViewBinding<DateTimeExecUIModel, RecyclerViewItem, ItemOrderEditDateBinding>(
            { layoutInflater, root -> ItemOrderEditDateBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    when {
                        item.isNotEmpty() -> {
                            etWorkDate.setText(item.start.toSimpleDateString())
                            val time = timeExecToString(item)
                            etWorkTime.setText(time)
                        }
                        item.start != LONG_DEFAULT -> {
                            etWorkDate.setText(item.start.toSimpleDateString())
                            etWorkTime.setText(R.string.filter_type_none)
                        }
                        else -> {
                            etWorkDate.setText(R.string.filter_type_none)
                            etWorkTime.setText(R.string.filter_type_none)
                        }
                    }
                    etWorkDate.setSafeOnClickListener { onDateFieldClick.invoke(item, position) }
                    etWorkTime.setSafeOnClickListener { onTimeFieldClick.invoke(item, position) }
                    ivClose.setSafeOnClickListener { onDateClear.invoke(item, position) }
                    //TODO
                }
            }
        }
}
