package com.gravitygroup.avangard.presentation.menu.about

import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.menu.about.AboutAppVm.AboutAppState

class AboutAppVm(
    handleState: SavedStateHandle
) : BaseViewModel<AboutAppState>(handleState, AboutAppState) {

    object AboutAppState: IViewModelState
}