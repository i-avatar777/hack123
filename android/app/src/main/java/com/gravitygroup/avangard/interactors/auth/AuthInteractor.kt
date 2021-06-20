package com.gravitygroup.avangard.interactors.auth

import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.local.SecurityPrefManager
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.di.network.AUTH_SMS_PENALTY_TIME
import com.gravitygroup.avangard.domain.auth.AuthData
import com.gravitygroup.avangard.domain.profile.ProfileData
import com.gravitygroup.avangard.interactors.auth.repository.AuthRepository

class AuthInteractor(
    private val prefManager: PreferenceManager,
    private val secManager: SecurityPrefManager,
    private val repository: AuthRepository,
) {

    suspend fun login(login: String, password: String, smsCode: String = ""): RequestResult<AuthData> =
        repository.login(login, password, smsCode).also {
            if(smsCode.isEmpty() && it is RequestResult.Success) { // запросили смс-код
                prefManager.authSmsTimeout = System.currentTimeMillis() + AUTH_SMS_PENALTY_TIME
            }
        }

    suspend fun logout(): RequestResult<Any> {
        secManager.doLogout()
        return repository.logout()
    }

    fun saveUserInfo(token: String, login: String, fio: String) {
        secManager.token = token
        secManager.login = login
        prefManager.fio = fio
    }

    fun profileData(): ProfileData {
        return ProfileData(
            login = secManager.login,
            fio = prefManager.fio,
            image = ""
        )
    }

    fun isAuthorized() = secManager.token.isNotEmpty()
}
