package com.gravitygroup.avangard.interactors.auth.repository

import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.network.base.BaseRequestResultHandler
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.core.network.errors.ErrorMapper
import com.gravitygroup.avangard.domain.auth.AuthData
import com.gravitygroup.avangard.domain.auth.LoginData
import com.gravitygroup.avangard.interactors.auth.api.AuthApi

class AuthRepositoryImpl(
    dispatchersProvider: DispatchersProvider,
    errorMapper: ErrorMapper,
    networkErrorBus: NetworkErrorBus,
    private val api: AuthApi,
    private val pref: PreferenceManager
) : AuthRepository, BaseRequestResultHandler(dispatchersProvider, errorMapper, networkErrorBus) {

    override suspend fun login(login: String, password: String, smsCode: String): RequestResult<AuthData> =
        call {
            api.login(LoginData(login, password, smsCode))
        }

    override suspend fun logout(): RequestResult<Any> =
        call {
            api.logout()
        }
}
