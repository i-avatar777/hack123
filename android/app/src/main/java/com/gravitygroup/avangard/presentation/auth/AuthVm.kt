package com.gravitygroup.avangard.presentation.auth

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.network.errors.ApiError
import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import kotlinx.coroutines.launch

class AuthVm(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val authInteractor: AuthInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<AuthState>(handleState, AuthState()) {

    fun onSetupView() {
        updateState { it.copy(smsTimeout = prefManager.authSmsTimeout) }
    }

    fun auth(login: String, password: String) {
        updateState { it.copy(isLoading = true) }
        launch {
            authInteractor
                .login(login, password, smsCode = STRING_DEFAULT)
                .handleResultWithError(
                    {
                        navigate(
                            NavigationCommand.Dir(
                                AuthFragmentDirections.actionToSmsCode(SmsCodeSpecs(login, password))
                            )
                        )
                        updateState {
                            it.copy(isLoading = false, errorMessage = "")
                        }
                    },
                    { t ->
                        val message = when(t) {
                            is ApiError.GeneralError -> context.getString(R.string.error_login_or_password)
                            else -> mapError(t)
                        }
                        updateState {
                            it.copy(
                                isLoading = false,
                                errorMessage = message
                            )
                        }
                    }
                )
        }
    }

    fun onFieldEdit() {
        prefManager.authSmsTimeout = 0L
        updateState { it.copy(smsTimeout = 0L) }
    }

    fun isAuthorized() {
        if (authInteractor.isAuthorized()) {
            navigate(NavigationCommand.FinishLogin)
        } else {
            navigate(NavigationCommand.StartLogin)
        }
    }

    fun isYachtAuthorized() {
        navigate(NavigationCommand.Yacht)
    }

    companion object {

        private const val STRING_DEFAULT = ""
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val smsTimeout: Long = 0L
) : IViewModelState
