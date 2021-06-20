package com.gravitygroup.avangard.core.network.interceptors

import com.gravitygroup.avangard.core.network.errors.NoNetworkError
import com.gravitygroup.avangard.utils.NetworkMonitor
import okhttp3.Interceptor
import okhttp3.Response

class NetworkStatusInterceptor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val isConnected = networkMonitor
            .connection
            .value
            ?.isConnected
            ?: false
        if (!isConnected) {
            throw NoNetworkError()
        }
        return chain.proceed(chain.request())
    }
}