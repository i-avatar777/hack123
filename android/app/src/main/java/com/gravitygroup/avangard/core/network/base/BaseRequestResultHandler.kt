package com.gravitygroup.avangard.core.network.base

import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.network.errors.BaseApiError
import com.gravitygroup.avangard.core.network.errors.ErrorMapper
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.core.network.ext.wrapResult
import kotlinx.coroutines.withContext

abstract class BaseRequestResultHandler(
    protected val dispatchersProvider: DispatchersProvider,
    private val errorMapper: ErrorMapper,
    private val errorBus: NetworkErrorBus
) {

    suspend fun <T, D> call(action: suspend () -> T): RequestResult<D>
            where T : BaseResponseObj<D> =
        withContext(dispatchersProvider.default()) {
            return@withContext when (val result = wrapResult { action() }) {
                is RequestResult.Error -> {
                    val mappedError = errorMapper.mapError<D, T>(result.error)
                    errorBus.postEvent(mappedError)
                    RequestResult.Error(mappedError)
                }
                is RequestResult.Success -> {
                    val response = result.data
                    when {
                        response?.code == CODE_SUCCESS -> {
                            RequestResult.Success(response.data)
                        }
                        else -> {
                            val mappedError = errorMapper.mapError<D, T>(
                                BaseApiError(
                                    response?.code ?: CODE_ERROR,
                                    response?.error ?: ""
                                )
                            )
                            errorBus.postEvent(mappedError)
                            RequestResult.Error(mappedError)
                        }
                    }
                }
            }
        }

    suspend fun <R, T, D> callAndMap(action: suspend () -> R): RequestResult<D>
            where R : BaseResponseObj<T>,
                  T : Transformable<D> =
        withContext(dispatchersProvider.default()) {
            return@withContext when (val result = wrapResult { action() }) {
                is RequestResult.Error -> {
                    val mappedError = errorMapper.mapError<T, R>(result.error, null)
                    errorBus.postEvent(mappedError)
                    RequestResult.Error(mappedError)
                }
                is RequestResult.Success -> {
                    val response = result.data
                    when {
                        response?.code == CODE_SUCCESS -> {
                            RequestResult.Success(response.data?.transform())
                        }
                        else -> {
                            val mappedError = errorMapper.mapError<T, R>(
                                    BaseApiError(
                                        response?.code ?: CODE_ERROR,
                                        response?.error ?: ""
                                    )
                                )
                            errorBus.postEvent(mappedError)
                            RequestResult.Error(mappedError)
                        }
                    }
                }
            }
        }

}
