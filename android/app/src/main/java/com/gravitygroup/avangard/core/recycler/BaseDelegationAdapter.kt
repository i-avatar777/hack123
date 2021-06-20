package com.gravitygroup.avangard.core.recycler

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class BaseDelegationAdapter(
    vararg adapterDelegates: AdapterDelegate<List<RecyclerViewItem>>
) : AsyncListDifferDelegationAdapter<RecyclerViewItem>(DiffUtilCallback) {

    init {
        adapterDelegates.forEach { delegatesManager.addDelegate(it) }
    }

    override fun setItems(items: List<RecyclerViewItem>) {
        this.setItems(items, null)
    }

    fun setItems(items: List<RecyclerViewItem>, commitCallback: (() -> Unit)?) {
        differ.submitList(items, commitCallback)
    }

}
