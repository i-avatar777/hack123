package com.gravitygroup.avangard.core.helper

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gravitygroup.avangard.R

class BlueMidlineItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val startMargin = context.resources.getDimensionPixelOffset(R.dimen.default_margin_10)
    private val divider = ContextCompat.getDrawable(context, R.drawable.divider_event_midline)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount <= 1) {
            return
        }

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            divider?.setBounds(
                startMargin,
                child.bottom,
                child.width,
                child.bottom + divider.intrinsicHeight
            )
            divider?.draw(c)
        }
    }
}
