package com.gravitygroup.avangard.presentation.yacht

import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentYachtFilterBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import org.koin.android.viewmodel.ext.android.sharedViewModel

class YachtFilterFragment : BaseFragment<YachtFilterVm>(R.layout.fragment_yacht_filter) {

    override val viewModel: YachtFilterVm by sharedViewModel()

    override val stateBinding by lazy { YachtFilterStateBinding() }

    private val vb by viewBinding(FragmentYachtFilterBinding::bind)

    override fun setupViews() {
        vb?.apply {
            icCheck.setSafeOnClickListener {
                val typeShare = when {
                    dayTypeRb.isChecked -> TypeShare.Day
                    hoursTypeRb.isChecked -> TypeShare.Hours
                    else -> TypeShare.Hours
                }
                val startPrice = etStartPrice.text.toString().takeIf { it.isNotEmpty() }?.toInt() ?: INT_DEFAULT
                val endPrice = etEndPrice.text.toString().takeIf { it.isNotEmpty() }?.toInt() ?: INT_DEFAULT
                val count = etCount.text.toString().takeIf { it.isNotEmpty() }?.toInt() ?: INT_DEFAULT

                viewModel.done(YachtListSpecs(count, typeShare.type, startPrice, endPrice))
            }
        }
    }

    inner class YachtFilterStateBinding : StateBinding() {

        override fun bind(data: IViewModelState) {
            data as YachtListState
        }
    }

    sealed class TypeShare(
        val type: Int
    ) {
        object Hours : TypeShare(1)
        object Day : TypeShare(2)
    }

    companion object {

        const val INT_DEFAULT = 0
    }
}