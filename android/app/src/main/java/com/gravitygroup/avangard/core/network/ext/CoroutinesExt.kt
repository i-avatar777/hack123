package com.gravitygroup.avangard.core.network.ext

import com.gravitygroup.avangard.core.network.base.RequestResult

inline fun <T> wrapResult(block: () -> T): RequestResult<T> {
    return try {
        RequestResult.Success(block())
    } catch (ex: Exception) {
        RequestResult.Error(ex)
    }
}