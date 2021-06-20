package com.gravitygroup.avangard.presentation.photo

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState

class PhotoVm(
        private val context: Context,
        private val prefManager: PreferenceManager,
        handleState: SavedStateHandle
) : BaseViewModel<PhotoState>(handleState, PhotoState()) {

}

data class PhotoState(
        val isLoading: Boolean = false
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}
