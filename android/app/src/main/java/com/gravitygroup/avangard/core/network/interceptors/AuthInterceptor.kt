package com.gravitygroup.avangard.core.network.interceptors

import com.gravitygroup.avangard.core.local.SecurityPrefManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val prefManager: SecurityPrefManager
) : Interceptor {

    companion object {

        const val HEADER_AUTH = "X-Auth-Token"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header(HEADER_AUTH) != null) {
            return chain.proceed(request)
        }
        val token = prefManager.token
        if (token.isBlank()) {
            return chain.proceed(request)
        }
        val authorizedRequest = request.newBuilder()
            .header(HEADER_AUTH, token)
            .build()
        return chain.proceed(authorizedRequest)
    }
}
