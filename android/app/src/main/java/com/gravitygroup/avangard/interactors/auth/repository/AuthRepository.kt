package com.gravitygroup.avangard.interactors.auth.repository

import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.domain.auth.AuthData

interface AuthRepository {

    suspend fun login(login: String, password: String, smsCode: String): RequestResult<AuthData>

    suspend fun logout(): RequestResult<Any>
}
