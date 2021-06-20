package com.gravitygroup.avangard.interactors.auth.api

import com.gravitygroup.avangard.core.network.base.BaseResponseObj
import com.gravitygroup.avangard.domain.auth.AuthData
import com.gravitygroup.avangard.domain.auth.LoginData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    /**
     * Вход в аккаунт
     **/
    @POST("login")
    suspend fun login(
        @Body request: LoginData
    ): BaseResponseObj<AuthData>

    /**
     * Выход из аккаунта
     **/
    @GET("logout")
    suspend fun logout(): BaseResponseObj<Any>

}