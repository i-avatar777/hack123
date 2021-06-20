package com.gravitygroup.avangard.presentation.yacht

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.gravitygroup.avangard.R

class DemoBottomSheetFragment : SuperBottomSheetFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_yacht_order, container, false)
    }

    override fun getCornerRadius() = requireContext().resources.getDimension(R.dimen.default_margin)

    override fun getStatusBarColor() = Color.RED
}