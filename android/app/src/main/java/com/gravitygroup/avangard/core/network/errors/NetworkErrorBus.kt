package com.gravitygroup.avangard.core.network.errors

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NetworkErrorBus {

    private val _errors = MutableSharedFlow<Throwable>()
    val events = _errors.asSharedFlow()

    suspend fun postEvent(event: Throwable) {
        _errors.emit(event)
    }

}