package com.gravitygroup.avangard.core.ext

import com.gravitygroup.avangard.core.network.base.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response

val Response.bodySnapshotUtf8: String?
    get() {
        val source = body?.source()
        source?.request(Long.MAX_VALUE)
        return source?.buffer?.snapshot()?.utf8()
    }

suspend inline fun <T> RequestResult<T>.handleResultWithError(
    noinline resultBlock: suspend (RequestResult.Success<T>) -> Unit,
    noinline errorBlock: suspend (Throwable) -> Unit
) = withContext(Dispatchers.Main) {
    when (val result = this@handleResultWithError) {
        is RequestResult.Success -> resultBlock(result)
        is RequestResult.Error -> errorBlock(result.error)
    }
}

suspend inline fun <T> RequestResult<T>.handleResult(
    noinline resultBlock: suspend (RequestResult.Success<T>) -> Unit
) = withContext(Dispatchers.Main) {
    when (val result = this@handleResult) {
        is RequestResult.Success -> resultBlock(result)
    }
}

fun <T> RequestResult<T>.handleSyncResult(
    resultBlock: (RequestResult.Success<T>) -> Unit
) {
    when (val result = this@handleSyncResult) {
        is RequestResult.Success -> resultBlock(result)
    }
}