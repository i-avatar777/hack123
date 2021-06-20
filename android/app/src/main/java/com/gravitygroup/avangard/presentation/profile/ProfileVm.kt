package com.gravitygroup.avangard.presentation.profile

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.profile.data.ProfileUIModel

class ProfileVm(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val authInteractor: AuthInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<ProfileState>(handleState, ProfileState()) {

    fun getProfileData() {
        val profileData = authInteractor.profileData()
        updateState {
            it.copy(
                profileData = ProfileUIModel(
                    login = profileData.login,
                    fio = profileData.fio,
                    image = profileData.image
                )
            )
        }
    }

}

data class ProfileState(
    val isLoading: Boolean = false,
    val profileData: ProfileUIModel = ProfileUIModel.empty()
) : IViewModelState {

    // optional save-restore implementation

    override fun save(outState: SavedStateHandle) {
        super.save(outState)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return super.restore(savedState)
    }
}
