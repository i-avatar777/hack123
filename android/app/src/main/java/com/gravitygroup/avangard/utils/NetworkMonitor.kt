package com.gravitygroup.avangard.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class NetworkMonitor(context: Context) {

    companion object {
        private const val WAIT_CONNECTION_TIME = 3000L
    }

    val connection = MutableLiveData(ConnectionData(true, NetworkType.UNKNOWN))

    private val cm: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var updateJob: Job? = null

    init {
        cm.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    update()
                }

                override fun onLost(network: Network) {
                    update(WAIT_CONNECTION_TIME)
                }
            }
        )
        update()
    }

    private fun update(delay: Long = 0) {
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Default).launch {
            delay(delay)
            obtainNetworkType(cm.activeNetwork?.let { cm.getNetworkCapabilities(it) })
                .also {
                    val isConnected = it != NetworkType.NONE
                    connection.postValue(ConnectionData(isConnected, it))
                }
        }
    }

    private fun obtainNetworkType(capabilities: NetworkCapabilities?): NetworkType = when {
        capabilities == null -> NetworkType.NONE
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
        else -> NetworkType.UNKNOWN
    }

}

data class ConnectionData(
    val isConnected: Boolean = true,
    val type: NetworkType = NetworkType.UNKNOWN
)

enum class NetworkType {
    NONE, UNKNOWN, WIFI, CELLULAR, ETHERNET
}