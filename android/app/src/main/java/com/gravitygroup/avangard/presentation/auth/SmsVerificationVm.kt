package com.gravitygroup.avangard.presentation.auth

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import kotlinx.coroutines.launch

class SmsVerificationVm(
    private val context: Context,
    private val prefManager: PreferenceManager,
    private val authInteractor: AuthInteractor,
    handleState : SavedStateHandle
) : BaseViewModel<SmsVerificationState>(handleState, SmsVerificationState())  {

    init {
        updateState { it.copy(smsTimeout = prefManager.authSmsTimeout) }
    }

    fun verifySmsCode(login: String, smsCode: String) {
        updateState { it.copy(isLoading = true, errorMessage = "") }

        launch {
            authInteractor
                .login(login = login, password = STRING_DEFAULT, smsCode = smsCode)
                .handleResultWithError(
                    { result ->
                        val data = result.data?.also { data ->
                            authInteractor.saveUserInfo(data.token, data.login, data.fio)
                            updateState { it.copy(isLoading = false) }
                            onAuthSuccess()
                        } ?: run{
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = context.getString(R.string.error_unknown)
                                )
                            }
                        }
                    },
                    { _ ->
                        updateState {
                            it.copy(
                                isLoading = false,
                                errorMessage = context.getString(R.string.error_sms_code)
                            )
                        }
                    }
                )
        }
    }

    fun onRequestSms(login: String, password: String) {
        updateState { it.copy(isLoading = true) }

        launch {
            authInteractor
                .login(login, password)
                .handleResultWithError(
                    {
                        updateState {
                            it.copy(
                                isLoading = false,
                                smsTimeout = prefManager.authSmsTimeout
                            )
                        }
                    },
                    { t ->
                        updateState {
                            it.copy(isLoading = false)
                        }
                        mapAndShowError(t)
                    }
                )
        }
    }

    fun onStartChange() {
        updateState { it.copy(errorMessage = "") }
    }

    private fun onAuthSuccess() {
        navigate(NavigationCommand.FinishLogin)
    }

    companion object {

        private const val STRING_DEFAULT = ""
    }
}

data class SmsVerificationState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val smsTimeout: Long = 0L,
) : IViewModelState