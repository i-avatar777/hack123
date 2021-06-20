package com.gravitygroup.avangard.core.recycler

import androidx.recyclerview.widget.DiffUtil

object DiffUtilCallback : DiffUtil.ItemCallback<RecyclerViewItem>() {
    override fun areItemsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem == newItem
    }
}
